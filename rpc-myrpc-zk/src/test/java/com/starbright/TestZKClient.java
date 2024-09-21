package com.starbright;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;

/**
 * @description: zk client 单元测试
 * @author: Star Bright
 * @date: 2024/9/20 21:56
 */
public class TestZKClient {

	private static final Logger log = LoggerFactory.getLogger(TestZKClient.class);
	private ZooKeeper zk;

	@Before
	public void before() throws IOException {
		// 多个节点使用,进行分割
		final String connectString = "localhost:2181";
		/*
		    参数一：zk集群的每一个节点 ip:2181
		    参数二：超时时间
		    参数三：监听器
		 */
		zk = new ZooKeeper(connectString, 2000, watchedEvent -> {
			log.info("zookeeper constructor event: {}", watchedEvent);
		});
	}


	/**
	 * 用于测试：创建节点，存放数据
	 */
	@Test
	public void test_createZNode() throws InterruptedException, KeeperException, IOException {
		/*
			参数一：节点名称
			参数二：节点存放的数据
			参数三：权限认证相关信息
			参数四：节点类型，持久节点、临时节点、持久有序节点、临时有序节点
		 */
		zk.create("/test", "test".getBytes(StandardCharsets.UTF_8), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		// 不存储数据
		zk.create("/test1", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		zk.create("/test2", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
		// 阻塞为了观察创建的临时节点
		Thread.sleep(2000);
	}

	/**
	 * 用于测试：修改节点
	 */
	@Test
	public void test_updateZNode() throws InterruptedException, KeeperException {
		/*
			参数一：修改的节点名称
			参数二：修改后的内容
			参数三：版本比对，-1代表不比对
		 */
		zk.setData("/test", "xiaohei".getBytes(StandardCharsets.UTF_8), -1);
	}

	/**
	 * 用于测试：查询节点
	 */
	@Test
	public void test_queryZNode() throws InterruptedException, KeeperException {
		/*
			参数一：查询的节点名称
			参数二：true的时候使用的是上面 new ZooKeeper() 构造方法的 watcher 进行监听使用, false：不进行监听
		 */
		List<String> testChildren = zk.getChildren("/test", false);
		testChildren.forEach(x -> log.info("test children: {}", x));

		/*
			参数一：查询的节点名称
			参数二：注册监听节点结构变化的事件
		 */
		List<String> test1Children = zk.getChildren("/test1", watchedEvent -> log.info("test1 watchedEvent: {}", watchedEvent));
		test1Children.forEach(x -> log.info("test1 children: {}", x));
		int nextInt = new Random().nextInt(10000);

		// 在 /test 节点下新增节点，用于触发上面注册的watcher
		zk.create("/test1/aa" + nextInt, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		zk.create("/test/aa" + nextInt, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	/**
	 * 用于测试：delete 操作
	 */
	@Test
	public void test_deleteZNode() throws InterruptedException, KeeperException {
		// 只能删除没有子元素的节点
		zk.delete("/test1", -1);
	}

	/**
	 * 用于测试：判断节点是否存在
	 */
	@Test
	public void test_zNode_exist() throws InterruptedException, KeeperException {
		// 监听的是未来的节点是否存在，未来创建了这个节点，会触发这个watcher
		zk.exists("/test", watchedEvent -> log.info("/test 节点存在"));

		// 判断现在的节点是否存在
		Stat exists = zk.exists("/test", false);
		log.info("exists: {}", exists);
	}



}
