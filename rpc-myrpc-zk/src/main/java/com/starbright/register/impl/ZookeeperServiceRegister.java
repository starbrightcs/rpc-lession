package com.starbright.register.impl;

import com.starbright.register.ServiceRegister;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: zk 服务注册
 * @author: Star Bright
 * @date: 2024/9/21 18:41
 */
public class ZookeeperServiceRegister implements ServiceRegister {

	private static final Logger log = LoggerFactory.getLogger(ZookeeperServiceRegister.class);
	private final CuratorFramework client;

	/**
	 * 服务注册的名称
	 */
	private final String servicePath;

	/**
	 * 注册的 IP
	 */
	private final String ip;

	/**
	 * 注册的端口
	 */
	private final String port;

	public ZookeeperServiceRegister(String connectString, String servicePath, String ip, String port) {
		this.servicePath = servicePath;
		this.ip = ip;
		this.port = port;
		ExponentialBackoffRetry backoffRetry = new ExponentialBackoffRetry(1000, 1000, 3);
		client = CuratorFrameworkFactory.newClient(connectString, 1000, 1000, backoffRetry);
		client.start();
	}

	@Override
	public void register() {
		// 1. 保证 /zk-register/servicePath 存在，不存在需要创建，存在则无需创建
		String basePath = "/zk-register";
		final String serviceNamePath = basePath + "/" + servicePath;
		try {
			Stat stat = client.checkExists().forPath(serviceNamePath);
			if (stat == null) {
				client.create().creatingParentsIfNeeded().forPath(serviceNamePath);
			}
			// 2. 创建 ip:port 这里是临时节点
			String urlNode = client.create().withMode(CreateMode.EPHEMERAL).forPath(serviceNamePath + "/" + ip + ":" + port);
			log.info("urlNode {}", urlNode);
		} catch (Exception e) {
			log.error("register error: {}", e.getMessage(), e);
			throw new RuntimeException("register error", e);
		}
	}

}
