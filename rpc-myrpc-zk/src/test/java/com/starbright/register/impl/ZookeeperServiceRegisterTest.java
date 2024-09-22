package com.starbright.register.impl;

import lombok.SneakyThrows;
import org.junit.Test;


/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/21 18:55
 */
public class ZookeeperServiceRegisterTest {

	@SneakyThrows
	@Test
	public void register() {
		// /zk-register/orderService/127.0.0.1:8080
		ZookeeperServiceRegister zookeeperServiceRegister = new ZookeeperServiceRegister("127.0.0.1:2181", "orderService", "127.0.0.1", "8080");
		zookeeperServiceRegister.register();

		Thread.sleep(3000);
	}
}
