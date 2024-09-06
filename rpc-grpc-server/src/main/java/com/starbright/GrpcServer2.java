package com.starbright;

import com.starbright.service.HelloServiceImpl;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/6 11:33
 */
public class GrpcServer2 {

	public static void main(String[] args) throws IOException, InterruptedException {
		ServerBuilder.forPort(9000)
				.addService(new HelloServiceImpl())
				.build().start().awaitTermination();
	}

}
