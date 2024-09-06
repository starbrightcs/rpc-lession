package com.starbright.service;

import com.starbright.HelloProto;
import com.starbright.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 业务接口实现
 * @author: Star Bright
 * @date: 2024/9/5 14:57
 */
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

	private static final Logger log = LoggerFactory.getLogger(HelloServiceImpl.class);

	/**
	 * 细心的小伙伴会发现这里和我们定义的proto的service接口不一致:
	 * rpc hello(HelloRequest) returns(HelloResponse) {};
	 * 我定义的明明是有返回值的，这里怎么就没有了呢？其实grpc的返回值和我们java中的不太一样，
	 * 这里使用了一个观察者模式，最终是通过StreamObserver进行返回的。
	 */
	@Override
	public void hello(HelloProto.HelloRequest request, StreamObserver<HelloProto.HelloResponse> responseObserver) {
		// 1. 拿到client请求的参数
		String name = request.getName();
		// 2. 业务处理，service+DAO等，这里不做业务处理，就简单打印一下参数
		log.info("name is {}", name);
		// 3. 提供返回值
		// 3.1 创建相关对象的构建者
		HelloProto.HelloResponse.Builder builder = HelloProto.HelloResponse.newBuilder();
		// 3.2 填充数据
		builder.setResult("hello method invoke ok");
		// 3.3 封装响应对象
		HelloProto.HelloResponse helloResponse = builder.build();

		// 把响应交给StreamObserver
		responseObserver.onNext(helloResponse);
		// 代表处理完成
		// responseObserver.onCompleted();
	}

	/**
	 * 服务端流式RPC开发
	 *
	 * @param request          请求参数
	 * @param responseObserver 返回值
	 */
	@Override
	public void c2ss(HelloProto.HelloRequest request, StreamObserver<HelloProto.HelloResponse> responseObserver) {
		String name = request.getName();
		log.info("name is {}", name);
		// 模拟服务端返回十次结果
		for (int i = 0; i < 10; i++) {
			responseObserver.onNext(
					HelloProto.HelloResponse.newBuilder()
							.setResult("c2ss method invoke ok, current i is " + i)
							.build()
			);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		responseObserver.onCompleted();
	}

	/**
	 * 客户端流式RPC，返回的是StreamObserver，为什么要这样设计？
	 * 因为客户端什么时候发送消息过来，对于服务端来说是不知道的，所以需要监听HelloRequest请求消息，通过观察者模式进行监控：
	 * 1. 客户端发送的数据
	 * 2. 异常
	 * 3. 全部消息是否发送完整
	 *
	 * @param responseObserver 响应StreamObserver
	 * @return 客户端StreamObserver
	 */
	@Override
	public StreamObserver<HelloProto.HelloRequest> cs2s(StreamObserver<HelloProto.HelloResponse> responseObserver) {
		return new StreamObserver<HelloProto.HelloRequest>() {
			@Override
			public void onCompleted() {
				log.info("服务端监听到客户端所有消息都发送完毕");
				// 提供响应，在处理完客户端发送的所有消息后，提供响应
				responseObserver.onNext(HelloProto.HelloResponse.newBuilder()
						.setResult("cs2s method invoke ok")
						.build());
				responseObserver.onCompleted();
			}

			@Override
			public void onNext(HelloProto.HelloRequest helloRequest) {
				log.info("服务端接收到客户端发送的一条消息: {}", helloRequest.getName());
			}

			@Override
			public void onError(Throwable throwable) {
				log.error("服务端监听客户端出现异常: {}", throwable.getMessage(), throwable);
			}
		};
	}

	/**
	 * 双向流式RPC服务端开发
	 *
	 * @param responseObserver 请求参数，用于处理响应
	 * @return 客户端请求参数
	 */
	@Override
	public StreamObserver<HelloProto.HelloRequest> css2ss(StreamObserver<HelloProto.HelloResponse> responseObserver) {
		return new StreamObserver<HelloProto.HelloRequest>() {
			@Override
			public void onCompleted() {
				log.info("服务端接收到客户端发送的全部消息");
				responseObserver.onCompleted();
			}

			@Override
			public void onNext(HelloProto.HelloRequest helloRequest) {
				log.info("服务端接收到客户端提交的一条消息 {}", helloRequest.getName());
				responseObserver.onNext(
						HelloProto.HelloResponse.newBuilder()
								.setResult("css2ss method invoke ok, param is " + helloRequest.getName())
								.build()
				);
			}

			@Override
			public void onError(Throwable throwable) {
			}
		};
	}

}
