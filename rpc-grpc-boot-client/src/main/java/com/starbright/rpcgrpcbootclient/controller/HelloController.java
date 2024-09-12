package com.starbright.rpcgrpcbootclient.controller;

import com.starbright.rpcgrpcbootclient.grpc.HelloProto;
import com.starbright.rpcgrpcbootclient.grpc.HelloServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/6 23:22
 */
@RestController
public class HelloController {

	private static final Logger log = LoggerFactory.getLogger(HelloController.class);
	// 从哪里获取，从grpc-server进行获取
	// @GrpcClient("grpc-server")
	@GrpcClient("cloud-grpc-server")
	private HelloServiceGrpc.HelloServiceBlockingStub helloServiceBlockingStub;


	@GetMapping("hello")
	public String hello(@RequestParam("name") String name) {
		log.info("name: {}", name);
		HelloProto.HelloResponse helloResponse = helloServiceBlockingStub.hello(
				HelloProto.HelloRequest.newBuilder()
						.setName(name)
						.build()
		);
		String result = helloResponse.getResult();
		log.info("result: {}", result);
		return result;
	}

}
