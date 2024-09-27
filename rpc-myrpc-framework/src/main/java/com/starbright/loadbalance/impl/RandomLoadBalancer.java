package com.starbright.loadbalance.impl;

import com.starbright.loadbalance.LoadBalancer;
import com.starbright.registry.HostAndPort;

import java.util.List;
import java.util.Random;

/**
 * @description: 随机轮询
 * @author: Star Bright
 * @date: 2024/9/25 18:00
 */
public class RandomLoadBalancer implements LoadBalancer {

	private final Random random = new Random();

	@Override
	public HostAndPort select(List<HostAndPort> hostAndPorts) {
		if (hostAndPorts == null || hostAndPorts.isEmpty()) {
			throw new IllegalStateException("hostAndPorts is null or empty");
		}
		return hostAndPorts.get(random.nextInt(hostAndPorts.size()));
	}

}
