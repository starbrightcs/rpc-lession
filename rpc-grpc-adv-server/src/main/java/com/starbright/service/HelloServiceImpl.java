package com.starbright.service;

import com.starbright.HelloProto;
import com.starbright.HelloServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/7 11:40
 */
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {

	private static final Logger log = LoggerFactory.getLogger(HelloServiceImpl.class);

	@Override
	public void hello(HelloProto.HelloRequest request, StreamObserver<HelloProto.HelloResponse> responseObserver) {
		log.info("参数: {}", request.getName());

		// 封装响应
		responseObserver.onNext(
				HelloProto.HelloResponse.newBuilder().setResult("this is result").build()
		);
		responseObserver.onCompleted();
	}

	@Override
	public StreamObserver<HelloProto.HelloRequest> hello1(StreamObserver<HelloProto.HelloResponse> responseObserver) {
		return new StreamObserver<HelloProto.HelloRequest>() {
			@Override
			public void onCompleted() {
				log.debug("request message all receive ...");
				responseObserver.onNext(HelloProto.HelloResponse.newBuilder().setResult("this is result1").build());
				responseObserver.onNext(HelloProto.HelloResponse.newBuilder().setResult("this is result2").build());
				responseObserver.onCompleted();
			}

			@Override
			public void onNext(HelloProto.HelloRequest helloRequest) {
				log.debug("request message is : {}", helloRequest.getName());
			}

			@Override
			public void onError(Throwable throwable) {

			}
		};
	}

}
