package com.starbright;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: grpc 客户端
 * @author: Star Bright
 * @date: 2024/9/5 15:21
 */
public class GrpcClient1 {

	private static final Logger log = LoggerFactory.getLogger(GrpcClient1.class);

	public static void main(String[] args) {
		// 1. 创建通信管道 Channel
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext().build();
		try {
			// 2. 创建代理 stub
			HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(managedChannel);
			// 3. 完成RPC调用
			HelloProto.HelloResponse response = helloService.hello(
					HelloProto.HelloRequest.newBuilder().setName("starbright").build()
			);
			log.info("invoke hello method response: {}", response);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			managedChannel.shutdownNow();
		}
	}

}
