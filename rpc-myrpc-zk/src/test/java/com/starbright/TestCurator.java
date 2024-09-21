package com.starbright;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

/**
 * @description: zk curator 单元测试
 * @author: Star Bright
 * @date: 2024/9/21 15:00
 */
public class TestCurator {
	private CuratorFramework client;

	@Before
	public void before() {
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
	}

	/**
	 * 用于测试：创建 zNode 节点
	 */
	@Test
	public void test_create() throws Exception {
		// ========== 持久节点 =========
		int nextInt = new Random().nextInt(1000);
		// 创建目录，即使不存放数据，也会默认的把 client 的 ip 地址设置进去当为数据
		client.create().forPath("/test" + nextInt);

		// 创建目录并显示指定数据
		nextInt = new Random().nextInt(1000);
		client.create().forPath("/test" + nextInt, "hello".getBytes(StandardCharsets.UTF_8));

		// 创建多级目录，需要注意的是数据给的是最后一个节点
		nextInt = new Random().nextInt(1000);
		client.create().creatingParentsIfNeeded().forPath("/z1/z2/z3" + nextInt, "world".getBytes(StandardCharsets.UTF_8));

		// ========== 创建不同类型节点, 根据 mode 选择对应的节点类型 =========
		client.create().withMode(CreateMode.EPHEMERAL).forPath("/aa", "aa".getBytes(StandardCharsets.UTF_8));
		client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/bb", "bb".getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * 用于测试：删除节点
	 */
	@Test
	public void test_delete() throws Exception {
		int nextInt = new Random().nextInt(1000);
		// 创建目录，即使不存放数据，也会默认的把 client 的 ip 地址设置进去当为数据
		client.create().forPath("/test" + nextInt);
		// 删除节点，只能删除单级节点，无法删除多级目录
		client.delete().forPath("/test" + nextInt);

		// 创建多级目录
		client.create().creatingParentsIfNeeded().forPath("/x1/x2/x3");
		// 删除多级目录
		client.delete().deletingChildrenIfNeeded().forPath("/x1");
	}

	/**
	 * 用于测试：修改数据
	 */
	@Test
	public void test_update() throws Exception {
		int nextInt = new Random().nextInt(1000);
		// 创建目录，即使不存放数据，也会默认的把 client 的 ip 地址设置进去当为数据
		client.create().forPath("/test" + nextInt, "hello".getBytes(StandardCharsets.UTF_8));
		// 修改内容
	    client.setData().forPath("/test" + nextInt, "world".getBytes(StandardCharsets.UTF_8));
		// 查询数据
		byte[] bytes = client.getData().forPath("/test" + nextInt);
		// 两者理应和修改后的数据一致
		Assert.assertArrayEquals(bytes, "world".getBytes(StandardCharsets.UTF_8));


		// client.create().creatingParentsIfNeeded().forPath("/x1/x2/x3");
		// 查询子节点
		List<String> childrenList = client.getChildren().forPath("/x1");
		childrenList.forEach(children -> System.out.println("x = " + children) );
	}


}
