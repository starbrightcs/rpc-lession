package com.starbright;

import com.starbright.service.HelloServiceImpl;
import com.starbright.streamtracer.CustomServerStreamTracerFactory;
import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

/**
 * @description: 服务端
 * @author: Star Bright
 * @date: 2024/9/7 11:40
 */
public class GrpcServer1 {

	public static void main(String[] args) throws IOException, InterruptedException {
		Server server = ServerBuilder.forPort(9000)
				.addService(new HelloServiceImpl())
				// 对应服务端的流式拦截器直接添加StreamTracerFactory即可，无需使用Interceptor进行封装
				.addStreamTracerFactory(new CustomServerStreamTracerFactory())
				.build()
				.start();

		server.awaitTermination();
	}

}
