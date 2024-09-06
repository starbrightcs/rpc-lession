package com.starbright;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * @description: 阻塞测试服务端流式RPC
 * @author: Star Bright
 * @date: 2024/9/6 11:34
 */
public class GrpcClient2 {

	private static final Logger log = LoggerFactory.getLogger(GrpcClient2.class);

	public static void main(String[] args) {
		// 1. 构建channel
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext().build();
		// 2. 创建代理
		HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(managedChannel);
		Iterator<HelloProto.HelloResponse> helloResponseIterator = helloService.c2ss(
				HelloProto.HelloRequest.newBuilder()
						.setName("starbright")
						.build()
		);
		helloResponseIterator.forEachRemaining(result -> log.info("result is {}", result));
	}

}
