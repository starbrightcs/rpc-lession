package com.starbright;

import com.starbright.resolver.CustomNameResolverProvider;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.NameResolverRegistry;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @description: nameResolver grpc客户端
 * @author: Star Bright
 * @date: 2024/9/12 11:29
 */
public class NameResolverGrpcClient {

	private static final Logger log = LoggerFactory.getLogger(NameResolverGrpcClient.class);

	@SneakyThrows
	public static void main(String[] args) {
		// 1. 引入命名解析
		NameResolverRegistry.getDefaultRegistry().register(new CustomNameResolverProvider());

		// 2. 客户端开发,这里需要使用集群的名字
		ManagedChannel managedChannel = ManagedChannelBuilder.forTarget("grpc-server")
				.usePlaintext()
				// 引入负载均衡算法:轮询
				.defaultLoadBalancingPolicy("round_robin")
				.build();

		// 3. 获取stub对象，进行grpc服务调用
		// 模拟发送4次请求，测试轮询的效果
		for (int i = 0; i < 4; i++) {
			sendRequest(managedChannel, i);
			Thread.sleep(1000);
		}

		managedChannel.awaitTermination(20, TimeUnit.SECONDS);
	}

	private static void sendRequest(ManagedChannel managedChannel, int idx) {
		HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(managedChannel);
		HelloProto.HelloResponse helloResponse = helloService.hello(HelloProto.HelloRequest.newBuilder().setName("starbright " + idx).build());
		log.info("idx: {}, result: {}", idx, helloResponse.getResult());
	}

}
