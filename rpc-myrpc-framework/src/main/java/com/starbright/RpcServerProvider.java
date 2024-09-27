package com.starbright;

import com.starbright.registry.HostAndPort;
import com.starbright.registry.Registry;
import com.starbright.registry.impl.ZookeeperRegistry;
import com.starbright.service.UserService;
import com.starbright.service.impl.UserServiceImpl;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;


/**
 * @description: rpc 服务端，具体功能如下：
 * <p/>
 * 1. 构建 netty 服务端
 * 2. 引入注册中心
 * 3. 引入所有的业务对象
 * 4. 通过反射进行调用
 * @author: Star Bright
 * @date: 2024/9/24 12:01
 */
public class RpcServerProvider {

	//  ====== netty server ======

	private static final Logger log = LoggerFactory.getLogger(RpcServerProvider.class);
	/**
	 * 端口号
	 */
	private final int port;

	/**
	 * 这里使用主从模式，boss 处理连接，worker 处理实际的 io 操作
	 */
	private EventLoopGroup eventLoopGroupBoss;
	private EventLoopGroup eventLoopGroupWorker;

	/**
	 * 处理 netty 编解码、内置的 handler 通过该线程组进行处理
	 */
	private EventLoopGroup eventLoopGroupHandler;

	/**
	 * 涉及业务的 handler 分配给该线程组
	 */
	private EventLoopGroup eventLoopGroupService;

	/**
	 * 指定线程数，需要注意 boss 实际运行的都是一个，所以不设置相关线程数了
	 */
	private int workerThreads;
	private int handlerThreads;
	private int serviceThreads;

	private ServerBootstrap serverBootstrap;

	private boolean isStarted = false;


	// ====== 注册中心 =======

	/**
	 * 服务注册
	 */
	private final Registry registry;

	/**
	 * 存放服务端提供功能的类对象，key:类全限定名 value:实现类对象
	 */
	private final Map<String, Object> exposeBeans;

	public RpcServerProvider(Registry registry, Map<String, Object> exposeBeans) {
		this(8080, 1, 1, 1, exposeBeans, registry);
	}

	public RpcServerProvider(int port, Registry registry, Map<String, Object> exposeBeans) {
		this(port, 1, 1, 1, exposeBeans, registry);
	}


	public RpcServerProvider(int port, Map<String, Object> exposeBeans, Registry registry) {
		this.port = port;
		this.exposeBeans = exposeBeans;
		this.registry = registry;

		initEventLoopGroup(1, 1, 1);
	}

	public RpcServerProvider(int port, int serviceThreads, int workerThreads, int handlerThreads, Map<String, Object> exposeBeans, Registry registry) {
		this.port = port;
		this.serviceThreads = serviceThreads;
		this.workerThreads = workerThreads;
		this.handlerThreads = handlerThreads;
		this.exposeBeans = exposeBeans;
		this.registry = registry;

		initEventLoopGroup(serviceThreads, workerThreads, handlerThreads);
	}

	public void initEventLoopGroup(int serviceThreads, int workerThreads, int handlerThreads) {
		eventLoopGroupBoss = new NioEventLoopGroup(1);
		eventLoopGroupWorker = new NioEventLoopGroup(workerThreads);
		eventLoopGroupHandler = new DefaultEventLoopGroup(handlerThreads);
		eventLoopGroupService = new DefaultEventLoopGroup(serviceThreads);
		serverBootstrap = new ServerBootstrap();
	}


	/**
	 * 开启服务
	 */
	public void startServer() throws UnknownHostException, InterruptedException {
		// 防止多次执行 startServer 方法，后面的 port 多次执行会报错，这里不用考虑多线程问题，因为这个对象每次都是 new 创建的
		if (isStarted) {
			throw new IllegalStateException("Server is already started");
		}

		// 1. Netty 服务端开发
		serverBootstrap.group(eventLoopGroupBoss, eventLoopGroupWorker);
		serverBootstrap.channel(NioServerSocketChannel.class);
		serverBootstrap.childHandler(new RpcServerProviderInitializer(this.eventLoopGroupHandler, this.eventLoopGroupService, exposeBeans));
		ChannelFuture channelFuture = serverBootstrap.bind(port);

		channelFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
			@Override
			public void operationComplete(Future<? super Void> future) throws Exception {
				if (future.isSuccess()) {
					log.info("Server started on port {}", port);
					// 2. 注册服务
					registerServices(InetAddress.getLocalHost().getHostAddress(), port, exposeBeans, registry);
					isStarted = true;

					// 监听关闭
					ChannelFuture closedFuture = channelFuture.channel().closeFuture();
					closedFuture.addListener(new GenericFutureListener<Future<? super Void>>() {
						@Override
						public void operationComplete(Future<? super Void> future) throws Exception {
							if (future.isSuccess()) {
								stopServer();
							}
						}
					});
				} else {
					log.error("Server start failed", future.cause());
				}
			}
		});

		// 处理非正常关闭，这里使用一个钩子函数
		Runtime.getRuntime().addShutdownHook(new Thread(this::stopServer));
	}

	/**
	 * 服务注册
	 *
	 * @param ip          ip 地址
	 * @param port        端口号
	 * @param exposeBeans 需要注册的 bean map
	 * @param registry    注册器
	 */
	public void registerServices(String ip, int port, Map<String, Object> exposeBeans, Registry registry) {
		// 1. 通过 exposeBeans 获取所有的服务对象信息
		Set<String> keySet = exposeBeans.keySet();

		// 2. 通过 registry 进行注册
		HostAndPort hostAndPort = new HostAndPort(ip, port);
		keySet.forEach(targetInterface -> {
			registry.register(targetInterface, hostAndPort);
			List<HostAndPort> serviceList = registry.getServiceList(targetInterface);
			registry.subscribeService(targetInterface, serviceList);
		});
	}

	/**
	 * 关闭服务，释放资源【优雅退出】
	 */
	public void stopServer() {
		eventLoopGroupBoss.shutdownGracefully();
		eventLoopGroupWorker.shutdownGracefully();
		eventLoopGroupHandler.shutdownGracefully();
		eventLoopGroupService.shutdownGracefully();
	}


	/*
		jdk8 以后: -Dio.netty.tryReflectionSetAccessible=true
	 */
	public static void main(String[] args) throws UnknownHostException, InterruptedException {
		// 这里暂时写死，后续可以考虑添加到 spring 进行管理
		Map<String, Object> exposeBeans = new HashMap<>();
		exposeBeans.put(UserService.class.getName(), new UserServiceImpl());
		ZookeeperRegistry registry = new ZookeeperRegistry("127.0.0.1:2181");
		RpcServerProvider rpcServerProvider = new RpcServerProvider(8081, exposeBeans, registry);
		rpcServerProvider.startServer();

		RpcServerProvider rpcServerProvider1 = new RpcServerProvider(8082, exposeBeans, registry);
		rpcServerProvider1.startServer();
		log.info("server start is ok....");
	}


}
