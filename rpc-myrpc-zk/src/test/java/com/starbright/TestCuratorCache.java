package com.starbright;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @description: Curator 监听操作
 * @author: Star Bright
 * @date: 2024/9/21 16:16
 */
public class TestCuratorCache {

	private static final Logger log = LoggerFactory.getLogger(TestCuratorCache.class);
	private CuratorFramework client;

	@Before
	public void before() throws Exception {
		/*
			设置 client 重试:
			参数一：连接不上时，最小的睡眠时间
			参数二：最大重试次数
			参数三：连接不上时，最大的睡眠时间
		 */
		ExponentialBackoffRetry backoffRetry = new ExponentialBackoffRetry(1000, 3, 3);
		// 存在在多个 zk 节点信息，使用,分割
		final String connectString = "127.0.0.1:2181";
		/*
			创建 CuratorFramework：
			参数一：连接的 zk 地址
			参数二：session 超时时间 对应的 jdbc 的 prepareStatement
			参数三：连接的超时时间 对应的 jdbc 的 connection
			参数四：client 重试
		 */
		client = CuratorFrameworkFactory.newClient(connectString, 3000, 3000, backoffRetry);
		// 开启客户端
		client.start();
		client.create().forPath("/starbright", "xiaohei".getBytes(StandardCharsets.UTF_8));
	}

	@After
	public void after() throws Exception {
		client.delete().forPath("/starbright");
	}

	/**
	 * 用于测试：NodeCacheListener 监听节点数据变化
	 * 下面这段代码，我们并没有获得这个节点新的数据，只能监听变化
	 */
	@Test
	public void test_NodeCacheListener() throws Exception {
		// CuratorCache
		CuratorCache curatorCache = CuratorCache.build(client, "/starbright");
		// NodeCacheListener
		CuratorCacheListener nodeCacheListener = CuratorCacheListener.builder().forNodeCache(new NodeCacheListener() {
			@Override
			public void nodeChanged() throws Exception {
				log.info("node value is changed");
			}
		}).build();
		// cacheListener 绑定到 cache
		curatorCache.listenable().addListener(nodeCacheListener);
		curatorCache.start();

		// 模拟修改数据
		new Thread(() -> {
			try {
				Thread.sleep(1000);
				client.setData().forPath("/starbright", "xiaobai".getBytes(StandardCharsets.UTF_8));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).start();

		// 阻塞等待一会，否则程序会直接关闭，观察不到对应的数据变化
		Thread.sleep(3000);
	}

	/**
	 * 用于测试：NodeCacheListener 监听节点数据变化，可以获得新值和旧值，推荐使用
	 */
	@Test
	public void test_NodeCacheListener1() throws Exception {
		// CuratorCache
		CuratorCache curatorCache = CuratorCache.build(client, "/starbright");
		// NodeCacheListener
		CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder().forChanges(new CuratorCacheListenerBuilder.ChangeListener() {
			@Override
			public void event(ChildData oldNode, ChildData node) {
				byte[] oldNodeBytes = oldNode.getData();
				byte[] newNodeBytes = node.getData();

				log.info("oldNode value is {}", new String(oldNodeBytes, StandardCharsets.UTF_8));
				log.info("newNode value is {}", new String(newNodeBytes, StandardCharsets.UTF_8));
			}
		}).build();
		// cacheListener 绑定到 cache
		curatorCache.listenable().addListener(curatorCacheListener);
		curatorCache.start();

		// 模拟修改数据
		new Thread(() -> {
			try {
				Thread.sleep(1000);
				client.setData().forPath("/starbright", "xiaobai".getBytes(StandardCharsets.UTF_8));
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).start();

		// 阻塞等待一会，否则程序会直接关闭，观察不到对应的数据变化
		Thread.sleep(3000);
	}

	/**
	 * 用于测试：监听子路径节点的变化的，可以包含孙子、重孙子节点 ... 不包含父目录
	 */
	@Test
	public void test_pathChildrenCacheListener() throws Exception {
		client.create().creatingParentsIfNeeded().forPath("/z1/z2");
		CuratorCache curatorCache = CuratorCache.build(client, "/z1/z2");
		CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder().forPathChildrenCache("/z1/z2", client, new PathChildrenCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				if (!event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)) {
					log.info("child is changed");
				}
				if (event.getType() == PathChildrenCacheEvent.Type.CHILD_ADDED) {
					log.info("child is add, path: {}", event.getData().getPath());
				}
			}
		}).build();

		curatorCache.listenable().addListener(curatorCacheListener);
		curatorCache.start();

		new Thread(() -> {
			try {
				Thread.sleep(1000);
				client.create().creatingParentsIfNeeded().forPath("/z1/z2/z3");
				Thread.sleep(500);
				client.create().creatingParentsIfNeeded().forPath("/z1/z2/z3/z4");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).start();

		Thread.sleep(3000);
		client.delete().deletingChildrenIfNeeded().forPath("/z1");
	}

	/**
	 * 用于测试：监听子路径节点的变化的，可以包含孙子、重孙子节点 ... 包含父目录,需要注意不能处理爷爷，只能处理到父亲节点
	 */
	@Test
	public void test_treeChildrenCacheListener() throws Exception {
		CuratorCache curatorCache = CuratorCache.build(client, "/z1/z2");
		CuratorCacheListener curatorCacheListener = CuratorCacheListener.builder().forTreeCache(client, new TreeCacheListener() {
			@Override
			public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
				log.info("---tree cache listener----");
			}
		}).build();

		curatorCache.listenable().addListener(curatorCacheListener);
		curatorCache.start();

		new Thread(() -> {
			try {
				Thread.sleep(1000);
				client.create().creatingParentsIfNeeded().forPath("/z1/z2/z3");
				Thread.sleep(500);
				client.create().creatingParentsIfNeeded().forPath("/z1/z2/z3/z4");
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).start();

		Thread.sleep(3000);
		client.delete().deletingChildrenIfNeeded().forPath("/z1");

	}

}
