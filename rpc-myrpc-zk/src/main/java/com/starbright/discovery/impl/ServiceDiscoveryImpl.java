package com.starbright.discovery.impl;

import com.starbright.discovery.ServiceDiscovery;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @description: 服务发现服务
 * @author: Star Bright
 * @date: 2024/9/22 10:53
 */
public class ServiceDiscoveryImpl implements ServiceDiscovery {

	private static final Logger log = LoggerFactory.getLogger(ServiceDiscoveryImpl.class);
	private final CuratorFramework client;

	public ServiceDiscoveryImpl(String connectString) {
		ExponentialBackoffRetry backoffRetry = new ExponentialBackoffRetry(1000, 1000, 3);
		client = CuratorFrameworkFactory.newClient(connectString, 1000, 1000, backoffRetry);
		client.start();
	}

	@Override
	public List<String> discover(String serviceName) {
		final String servicePath = "/zk-register/" + serviceName;
		try {
			Stat stat = client.checkExists().forPath(servicePath);
			if (stat == null) {
				throw new RuntimeException("Can not find service " + servicePath);
			}
			return client.getChildren().forPath(servicePath);
		} catch (Exception e) {
			log.error("Discovery execute exception {}", e.getMessage(), e);
			throw new RuntimeException("Discovery execute exception " + e.getMessage(), e);
		}
	}

	@Override
	public void registerWatch(String serviceName) {
		final String servicePath = "/zk-register/" + serviceName;
		CuratorCache curatorCache = CuratorCache.builder(client, servicePath).build();
		// 只需要监控 /zk-register/OrderService 节点下的集群信息变化即可，无需关心父节点
		CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder()
				.forPathChildrenCache(servicePath, client, new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
				// 获取最新的服务列表数据
				List<String> childrenList = client.getChildren().forPath(servicePath);
				log.info("ServicePath: {} is changed, new Children: {}", servicePath, childrenList);
			}
		}).build();
		curatorCache.listenable().addListener(curatorCacheListener);
		curatorCache.start();
	}

}
