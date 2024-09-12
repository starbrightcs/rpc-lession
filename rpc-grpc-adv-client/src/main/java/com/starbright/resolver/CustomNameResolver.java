package com.starbright.resolver;

import com.orbitz.consul.Consul;
import com.orbitz.consul.model.health.ServiceHealth;
import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;
import io.grpc.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @description: 自定义NameResolver
 * @author: Star Bright
 * @date: 2024/9/12 09:29
 */
public class CustomNameResolver extends NameResolver {

	private static final Logger log = LoggerFactory.getLogger(CustomNameResolver.class);

	private final String authority;

	private final Consul consul;

	private Listener2 listener;

	private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(10);

	public CustomNameResolver(String authority) {
		this.authority = authority;
		this.consul = Consul.builder().build();
	}

	/**
	 * authority：服务器集群的名字
	 *
	 * @return 服务器集群的名字
	 */
	@Override
	public String getServiceAuthority() {
		return this.authority;
	}

	/**
	 * grpc 调用该方法
	 * <p>
	 * start方法中我们需要周期的发起对名字解析的请求，获取最新的服务列表
	 */
	@Override
	public void start(Listener2 listener) {
		this.listener = listener;
		// 发起对consul注册中心的查找【定时周期】
		scheduledThreadPoolExecutor.scheduleAtFixedRate(this::resolve, 10, 10, TimeUnit.SECONDS);
	}

	/**
	 * 定时完成的工作在该方法进行处理，完成解析的工作
	 */
	private void resolve() {
		log.debug("开始解析我们的服务: {}", authority);
		// 1. 获取所有的健康服务
		List<InetSocketAddress> addressList = getAddressList(this.authority);
		if (addressList.isEmpty()) {
			log.error("解析存在问题，服务: {} 获取的健康节点为空", authority);
			listener.onError(Status.UNAVAILABLE.withDescription(String.format("服务[%s]没有可用的节点", authority)));
			return;
		}

		// 2. 负载均衡 grpc 负责，我们只需要把所有的健康服务封装成
		List<EquivalentAddressGroup> equivalentAddressGroupList = addressList.stream()
				.map(EquivalentAddressGroup::new)
				.collect(Collectors.toList());

		// 3. 命名解析完成 ResolutionResult
		ResolutionResult resolutionResult = ResolutionResult.newBuilder()
				.setAddresses(equivalentAddressGroupList)
				.build();

		// 4. 监听名字解析的结果
		listener.onResult(resolutionResult);
	}

	private List<InetSocketAddress> getAddressList(String authority) {
		return consul.healthClient()
				.getHealthyServiceInstances(authority)
				.getResponse()
				.stream()
				.map(ServiceHealth::getService)
				.map(service -> new InetSocketAddress(service.getAddress(), service.getPort()))
				.collect(Collectors.toList());
	}

	@Override
	public void refresh() {
		super.refresh();
	}

	/**
	 * 关闭，这里就是关闭线程池
	 */
	@Override
	public void shutdown() {
		scheduledThreadPoolExecutor.shutdown();
	}

}
