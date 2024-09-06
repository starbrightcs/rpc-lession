package com.starbright;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @description: 监听服务端流式RPC
 * @author: Star Bright
 * @date: 2024/9/6 14:47
 */
public class GrpcClient3 {

	private static final Logger log = LoggerFactory.getLogger(GrpcClient3.class);

	public static void main(String[] args) {
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext().build();
		try {
			HelloServiceGrpc.HelloServiceStub helloService = HelloServiceGrpc.newStub(managedChannel);

			// 这里通过观察者的模式进行监听服务端的响应
			helloService.c2ss(
					HelloProto.HelloRequest.newBuilder()
							.setName("starbright")
							.build(),

					new StreamObserver<HelloProto.HelloResponse>() {

						@Override
						public void onCompleted() {
							// 需要等服务端都处理完成才进行处理，代码写在这里
							log.info("服务端响应结束，后续可以根据需要进行统一处理服务端响应的内容");
						}

						@Override
						public void onNext(HelloProto.HelloResponse helloResponse) {
							// 服务端响应了一个消息后，需要立即处理的话，代码写在这里
							log.info("服务端每一次响应的信息: {}", helloResponse);
						}

						@Override
						public void onError(Throwable throwable) {
							// 服务端出现异常后，需要做什么处理
							log.error("服务端出现异常, e: {}", throwable.getMessage(), throwable);
						}
					}
			);

			managedChannel.awaitTermination(60, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			managedChannel.shutdownNow();
		}
	}

}
