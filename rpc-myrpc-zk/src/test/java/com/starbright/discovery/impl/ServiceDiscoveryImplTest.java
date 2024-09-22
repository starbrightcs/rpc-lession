package com.starbright.discovery.impl;

import org.junit.Test;

import java.util.List;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/22 11:03
 */
public class ServiceDiscoveryImplTest {


	@Test
	public void discover() {
		List<String> serviceList = new ServiceDiscoveryImpl("127.0.0.1:2181").discover("/OrderService");
		serviceList.forEach(System.out::println);
	}
}
