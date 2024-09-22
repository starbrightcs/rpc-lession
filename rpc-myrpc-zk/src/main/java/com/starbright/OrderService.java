package com.starbright;

import com.starbright.register.impl.ZookeeperServiceRegister;

import java.io.IOException;

/**
 * @description: 模拟 RPC 服务集群，最终通过ServiceRegister进行注册
 * @author: Star Bright
 * @date: 2024/9/21 19:09
 */
public class OrderService {

	public static void main(String[] args) throws IOException {
		// 1. 服务名称
		String serviceName = OrderService.class.getSimpleName();
		// 2. 服务的 IP
		String ip = System.getenv("ip");
		// 3. 服务的 port
		String port = System.getenv("port");

		final String connectString = "127.0.0.1:2181";
		new ZookeeperServiceRegister(connectString, serviceName, ip, port).register();

		// 后续通过 Netty 的 ServerBootStrap 进行注册

		System.in.read();
	}

}
