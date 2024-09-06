package com.starbright.service;

import com.starbright.TestProto;
import com.starbright.TestServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: testService实现类
 * @author: Star Bright
 * @date: 2024/9/6 17:58
 */
public class TestServiceImpl extends TestServiceGrpc.TestServiceImplBase {

	private static final Logger log = LoggerFactory.getLogger(TestServiceImpl.class);

	@Override
	public void test(TestProto.TestRequest request, StreamObserver<TestProto.TestResponse> responseObserver) {
		log.info("请求参数: {}", request.getName());

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		responseObserver.onNext(TestProto.TestResponse.newBuilder().setResult("test method invoke is ok").build());
		responseObserver.onCompleted();
	}

}
