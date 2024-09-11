package com.starbright;

import com.alibaba.fastjson.JSON;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @description: 客户端重试
 * @author: Star Bright
 * @date: 2024/9/10 16:12
 */
public class GrpcClientRetry {

	private static final Logger log = LoggerFactory.getLogger(GrpcClientRetry.class);

	public static void main(String[] args) {
		Map<String, ?> serviceConfig = getServiceConfig();

		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9000)
				.usePlaintext()
				.defaultServiceConfig(serviceConfig)
				.enableRetry()
				.build();

		try {
			HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(managedChannel);
			HelloProto.HelloResponse helloResponse = helloService.hello(
					HelloProto.HelloRequest.newBuilder().setName("hello").build()
			);
			log.info("result: {}", helloResponse.getResult());

			managedChannel.awaitTermination(2, TimeUnit.SECONDS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			managedChannel.shutdown();
		}

	}

	@SneakyThrows
	private static Map<String, ?> getServiceConfig() {
		// 1. 读取文件
		File configFile = new File("/Users/baiyi/github/rpc-lession/rpc-grpc-adv-client/src/main/resources/service_config.json");
		Path path = configFile.toPath();
		byte[] bytes = Files.readAllBytes(path);

		// 2. 文件json内容转换为java对象
		return JSON.parseObject(bytes, Map.class);
	}

}
