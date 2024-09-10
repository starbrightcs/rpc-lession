package com.starbright;

import com.starbright.streamtracer.CustomClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/9 15:20
 */
public class GrpcClient1 {

	private static final Logger log = LoggerFactory.getLogger(GrpcClient1.class);

	public static void main(String[] args) {
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000)
				.intercept(new CustomClientInterceptor())
				.usePlaintext()
				.build();

		try {
			HelloServiceGrpc.HelloServiceStub helloService = HelloServiceGrpc.newStub(managedChannel);
			StreamObserver<HelloProto.HelloRequest> helloRequestStreamObserver = helloService.hello1(
					new StreamObserver<HelloProto.HelloResponse>() {
						@Override
						public void onCompleted() {
							log.debug("client 接收到所有的响应内容，响应结束...");
						}

						@Override
						public void onNext(HelloProto.HelloResponse helloResponse) {
							log.debug("client 接收到服务端的响应: {}", helloResponse.getResult());
						}

						@Override
						public void onError(Throwable throwable) {

						}
					}
			);

			for (int i = 0; i < 3; i++) {
				helloRequestStreamObserver.onNext(HelloProto.HelloRequest.newBuilder().setName("name " + i).build());
			}
			helloRequestStreamObserver.onCompleted();

			managedChannel.awaitTermination(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			managedChannel.shutdown();
		}
	}

}
