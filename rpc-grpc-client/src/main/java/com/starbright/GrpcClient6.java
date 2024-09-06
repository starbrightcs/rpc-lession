package com.starbright;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @description: futureSub测试
 * @author: Star Bright
 * @date: 2024/9/6 18:02
 */
public class GrpcClient6 {

	private static final Logger log = LoggerFactory.getLogger(GrpcClient6.class);

	public static void main(String[] args) {
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000).usePlaintext().build();
		try {
			TestServiceGrpc.TestServiceFutureStub serviceFutureStub = TestServiceGrpc.newFutureStub(managedChannel);
			ListenableFuture<TestProto.TestResponse> responseListenableFuture = serviceFutureStub.test(
					TestProto.TestRequest.newBuilder().setName("starbright").build()
			);

			// 同步的阻塞等待服务端请求
			/* TestProto.TestResponse testResponse = responseListenableFuture.get();
			log.info("response: {}", testResponse.getResult()); */

			// 异步的方式监听客户端响应，该代码不能用于实战，获取不到响应结果
			// responseListenableFuture.addListener(() -> log.info("异步的RPC响应回来了"), Executors.newSingleThreadExecutor());

			// 异步处理
			Futures.addCallback(responseListenableFuture,
					new FutureCallback<TestProto.TestResponse>() {
						@Override
						public void onFailure(Throwable t) {
							log.info("服务端调用失败", t);
						}

						@Override
						public void onSuccess(TestProto.TestResponse result) {
							log.info("服务响应结果为: {}", result.getResult());
						}
					}, Executors.newSingleThreadExecutor());

			// 如果使用同步阻塞的代码会被阻塞，直到服务端返回
			log.info("后续的操作");

			managedChannel.awaitTermination(20, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			managedChannel.shutdownNow();
		}
	}

}
