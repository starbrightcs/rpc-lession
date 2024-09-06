package com.starbright;

import com.starbright.service.HelloServiceImpl;
import com.starbright.service.TestServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @description: grpc服务
 * @author: Star Bright
 * @date: 2024/9/5 15:10
 */
public class GrpcServer1 {

	private static final Logger log = LoggerFactory.getLogger(GrpcServer1.class);

	public static void main(String[] args) {
		try {
			Server server = ServerBuilder.forPort(9000) // 绑定端口
					.addService(new HelloServiceImpl()) // 添加需要发布的服务
					.addService(new TestServiceImpl())
					.build();
			// 启动server并等待
			server.start();
			server.awaitTermination();
		} catch (IOException | InterruptedException e) {
			log.error("grpc server exception: {}", e.getMessage(), e);
		}
	}

}
