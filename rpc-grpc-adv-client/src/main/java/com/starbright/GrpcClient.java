package com.starbright;

import com.starbright.interceptor.CustomClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @description: grpc客户端
 * @author: Star Bright
 * @date: 2024/9/7 11:37
 */
public class GrpcClient {

	private static final Logger log = LoggerFactory.getLogger(GrpcClient.class);

	public static void main(String[] args) {
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000)
				.usePlaintext()
				.intercept(new CustomClientInterceptor()) // 设置拦截器
				.build();

		try {
			HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(managedChannel);

			HelloProto.HelloResponse helloResponse = helloService.hello(
					HelloProto.HelloRequest.newBuilder().setName("starbright").build()
			);

			log.info("result: {}", helloResponse.getResult());

			managedChannel.awaitTermination(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			managedChannel.shutdown();
		}
	}

}
