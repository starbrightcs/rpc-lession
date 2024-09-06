package com.starbright.rpcgrpcbootserver.service;

import com.starbright.rpcgrpcbootserver.grpc.HelloProto;
import com.starbright.rpcgrpcbootserver.grpc.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: HelloService 实现类
 * @author: Star Bright
 * @date: 2024/9/6 21:57
 */
@GrpcService
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

	private static final Logger log = LoggerFactory.getLogger(HelloServiceImpl.class);

	@Override
	public void hello(HelloProto.HelloRequest request, StreamObserver<HelloProto.HelloResponse> responseObserver) {
		// 获取请求参数
		String name = request.getName();
		log.info("参数: {}", name);
		// 构建响应
		responseObserver.onNext(
				HelloProto.HelloResponse.newBuilder()
						.setResult("hello method invoke ok")
						.build()
		);
		// 返回给客户端
		responseObserver.onCompleted();
	}
}
