package com.starbright;

import com.starbright.discovery.impl.RandomLoadBalance;
import com.starbright.discovery.impl.ServiceDiscoveryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * @description: 客户端
 * @author: Star Bright
 * @date: 2024/9/22 11:04
 */
public class Client {

	private static final Logger log = LoggerFactory.getLogger(Client.class);

	public static void main(String[] args) throws IOException {
		// 每一次都获取
		List<String> discover = new ServiceDiscoveryImpl("localhost:2181").discover("OrderService");
		log.info("discover: {}", discover);

		// 在服务列表中选择一个节点 【负载均衡】
		String currentUrl = new RandomLoadBalance().select(discover);
		log.info("currentUrl: {}", currentUrl);

		/*
		    获取 url 进行实际的访问
		    1. SpringBoot 发布一个服务 web 形式，可以使用 web 形式、httpclient、restTemplate、okhttp 进行访问
		    2. Netty client 获得服务器端的 ip:port 进行连接即可实际通信
		 */

		// 监听的方式进行获取
		new ServiceDiscoveryImpl("localhost:2181").registerWatch("OrderService");

		System.in.read();
	}

}
