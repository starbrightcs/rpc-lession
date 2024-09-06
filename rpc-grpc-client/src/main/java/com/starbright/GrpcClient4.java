package com.starbright;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @description: 客户端流式RPC
 * @author: Star Bright
 * @date: 2024/9/6 16:17
 */
public class GrpcClient4 {

	private static final Logger log = LoggerFactory.getLogger(GrpcClient4.class);

	public static void main(String[] args) {
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext().build();

		try {
			HelloServiceGrpc.HelloServiceStub helloService = HelloServiceGrpc.newStub(managedChannel);

			// 通过返回值进行发送消息，这里的参数是监控服务端的响应
			StreamObserver<HelloProto.HelloRequest> helloRequestStreamObserver = helloService.cs2s(
					new StreamObserver<HelloProto.HelloResponse>() {
						@Override
						public void onCompleted() {
							log.info("服务端处理完成");
						}

						@Override
						public void onNext(HelloProto.HelloResponse helloResponse) {
							log.info("服务端响应一次数据：{}", helloResponse);
						}

						@Override
						public void onError(Throwable throwable) {
							log.error(throwable.getMessage());
						}
					}
			);

			// 发送消息
			for (int i = 0; i < 10; i++) {
				helloRequestStreamObserver.onNext(HelloProto.HelloRequest.newBuilder().setName("current i = " + i).build());
			}

			// 发送消息完成
			helloRequestStreamObserver.onCompleted();

			managedChannel.awaitTermination(30, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			managedChannel.shutdown();
		}
	}

}
