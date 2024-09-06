package com.starbright;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @description: 双向流式RPC客户端
 * @author: Star Bright
 * @date: 2024/9/6 16:51
 */
public class GrpcClient5 {

	private static final Logger log = LoggerFactory.getLogger(GrpcClient5.class);

	public static void main(String[] args) {
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext().build();
		try {
			HelloServiceGrpc.HelloServiceStub helloService = HelloServiceGrpc.newStub(managedChannel);
			StreamObserver<HelloProto.HelloRequest> helloRequestStreamObserver = helloService.css2ss(new StreamObserver<HelloProto.HelloResponse>() {
				@Override
				public void onNext(HelloProto.HelloResponse helloResponse) {
					log.info("服务端响应数据: {}", helloResponse.getResult());
				}

				@Override
				public void onError(Throwable throwable) {

				}

				@Override
				public void onCompleted() {
					log.info("服务端响应结束");
				}
			});

			// 发送数据
			for (int i = 0; i < 5; i++) {
				helloRequestStreamObserver.onNext(
						HelloProto.HelloRequest.newBuilder()
								.setName("message " + i)
								.build()
				);
			}

			helloRequestStreamObserver.onCompleted();

			managedChannel.awaitTermination(20, TimeUnit.SECONDS);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			managedChannel.shutdown();
		}
	}

}
