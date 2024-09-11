package com.starbright;

import com.starbright.service.HelloServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * @description: 服务端
 * @author: Star Bright
 * @date: 2024/9/7 11:40
 */
public class GrpcServer {

	public static void main(String[] args) throws IOException, InterruptedException {
		Server server = ServerBuilder.forPort(9000)
				.addService(new HelloServiceImpl())
				// .intercept(new CustomServerInterceptor())
				.build()
				.start();

		server.awaitTermination();
	}

}
