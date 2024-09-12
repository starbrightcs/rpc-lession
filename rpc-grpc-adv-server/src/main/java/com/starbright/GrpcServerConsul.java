package com.starbright;

import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.model.agent.ImmutableRegistration;
import com.orbitz.consul.model.agent.Registration;
import com.starbright.service.HelloServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Random;
import java.util.UUID;

/**
 * @description: grpc服务端注册到consul中
 * @author: Star Bright
 * @date: 2024/9/11 16:36
 */
public class GrpcServerConsul {

	private static final Logger log = LoggerFactory.getLogger(GrpcServerConsul.class);

	@SneakyThrows
	public static void main(String[] args) {
		int port = new Random().nextInt(65535);

		Server server = ServerBuilder.forPort(port)
				.addService(new HelloServiceImpl())
				.build().start();

		log.debug("server is start...");
		registerToConsul("127.0.0.1", port);
		server.awaitTermination();
	}

	private static void registerToConsul(String address, int port) {
		Consul consulConnection = Consul.builder().build();
		AgentClient agentClient = consulConnection.agentClient();

		ImmutableRegistration service = ImmutableRegistration.builder()
				.id("Server-" + UUID.randomUUID())
				.name("grpc-server")
				.address(address)
				.port(port)
				.tags(Collections.singletonList("server"))
				.meta(Collections.singletonMap("version", "1.0.0"))
				// 需要注意grpc底层通信协议还是tcp，应用层协议为http2.0
				.check(Registration.RegCheck.tcp(address + ":" + port, 5))
				.build();

		agentClient.register(service);
	}

}
