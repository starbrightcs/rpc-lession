package com.starbright;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import lombok.SneakyThrows;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

/**
 * @description: 模拟服务注册到consul上
 * @author: Star Bright
 * @date: 2024/9/11 15:00
 */
public class TestServer {

	@SneakyThrows
	public static void main(String[] args) {
		int port = new Random().nextInt(65539);
		// 1. 模拟服务 localhost:9000
		ServerSocketChannel serverSocketChannel = ServerSocketChannel.open().bind(new InetSocketAddress(port));

		// 2. 使用java consul客户端注册服务
		// consul 服务器，默认是连接本机的consul，也就是localhost:8500
		Consul consulConnection = Consul.builder().build();

		// 获得consul客户端对象
		AgentClient agentClient = consulConnection.agentClient();

		// 进行服务注册
		ImmutableRegistration service = ImmutableRegistration.builder()
				// 集群上逻辑上的名字
				.name("grpc-server")
				// 唯一标识，便于区分集群中的每一个服务
				.id("Server-" + UUID.randomUUID())
				.address("localhost")
				.port(port)
				// 标识
				.tags(Collections.singletonList("grpc-server"))
				.meta(Collections.singletonMap("version", "1.0"))
				// 健康检查 ttl tcp http，这里选择tcp方式，每隔10s进行检查一遍
				.check(Registration.RegCheck.tcp("localhost:" + port, 10))
				.build();

		// 注册服务到consul上
		agentClient.register(service);

		// 模拟服务一直运行
		Thread.sleep(100 * 1000L);
	}

}
