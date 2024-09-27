package com.starbright.registry.impl;

import com.starbright.registry.HostAndPort;
import com.starbright.registry.Registry;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: zk注册中心
 * @author: Star Bright
 * @date: 2024/9/25 11:37
 */
public class ZookeeperRegistry implements Registry {

	private static final Logger log = LoggerFactory.getLogger(ZookeeperRegistry.class);
	private final CuratorFramework client;

	public ZookeeperRegistry(String zkServerAddress) {
		this.client = CuratorFrameworkFactory.newClient(zkServerAddress, 1000, 1000, new ExponentialBackoffRetry(1000, 3));
		client.start();
	}

	@Override
	public void register(String targetInterfaceName, HostAndPort hostAndPort) {
		final String servicePath = SERVICE_PREFIX + targetInterfaceName + SERVICE_SUFFIX;

		try {
			if (client.checkExists().forPath(servicePath) == null) {
				// 创建持久节点 /rpc/服务名称/provider/
				client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath);
			}
			// 挂载 ip:port
			String nodeUrl = client.create().withMode(CreateMode.EPHEMERAL).forPath(servicePath + "/" + hostAndPort.getHostName() + ":" + hostAndPort.getPort());
			log.info("node is created {}", nodeUrl);
		} catch (Exception e) {
			log.error("register error {}", e.getMessage(), e);
			throw new RuntimeException("register error", e);
		}

	}

	@Override
	public List<HostAndPort> getServiceList(String targetInterfaceName) {
		final String servicePath = SERVICE_PREFIX + targetInterfaceName + SERVICE_SUFFIX;
		try {
			if (this.client.checkExists().forPath(servicePath) != null) {
				// ip:port
				return transferServiceListToHostAndName(this.client.getChildren().forPath(servicePath));
			} else {
				throw new RuntimeException(String.format("Can not get service list from %s", servicePath));
			}
		} catch (Exception e) {
			log.error("Node {} get service list error {}", servicePath, e.getMessage(), e);
			throw new RuntimeException(String.format("Node %s get service list error", servicePath), e);
		}
	}

	@Override
	public void subscribeService(String targetInterfaceName, List<HostAndPort> existHostAndPorts) {
		final String servicePath = SERVICE_PREFIX + targetInterfaceName + SERVICE_SUFFIX;
		// 监听使用 CuratorCache、CuratorCacheListener
		CuratorCache curatorCache = CuratorCache.build(client, servicePath);
		CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder().forPathChildrenCache(servicePath, client, new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				// 1 目前服务列表中的数据清除掉
				existHostAndPorts.clear();
				// 2 获取最新的服务列表数据 client.getChildren().forPath(servicePath) --> List<String>
				existHostAndPorts.addAll(transferServiceListToHostAndName(client.getChildren().forPath(servicePath)));
			}
		}).build();

		curatorCache.listenable().addListener(curatorCacheListener);
		curatorCache.start();
	}

	private List<HostAndPort> transferServiceListToHostAndName(List<String> serviceList) {
		return serviceList.stream()
				// string[0] host string[1] port
				.map(s -> s.split(":"))
				.map(sa -> new HostAndPort(sa[0], Integer.parseInt(sa[1])))
				.collect(Collectors.toList());
	}

}
