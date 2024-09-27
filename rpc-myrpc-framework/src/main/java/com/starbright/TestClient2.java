package com.starbright;

import com.starbright.cluster.impl.FailFastCluster;
import com.starbright.loadbalance.impl.RandomLoadBalancer;
import com.starbright.proxy.JDKProxy;
import com.starbright.registry.impl.ZookeeperRegistry;
import com.starbright.service.UserService;
import com.starbright.transport.impl.NettyTransport;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/26 16:47
 */
public class TestClient2 {

	public static void main(String[] args) {
		JDKProxy jdkProxy = new JDKProxy(UserService.class);
		jdkProxy.setCluster(new FailFastCluster());
		jdkProxy.setTransport(new NettyTransport());
		jdkProxy.setRegistry(new ZookeeperRegistry("127.0.0.1:2181"));
		jdkProxy.setLoadBalancer(new RandomLoadBalancer());
		UserService userService = (UserService) jdkProxy.createProxy();

		boolean result = userService.login("starbright", "123456");
		System.out.println("result = " + result);
	}


}
