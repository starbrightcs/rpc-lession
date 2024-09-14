package com.starbright;

import com.starbright.serializer.impl.ThriftSerializer;
import lombok.SneakyThrows;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/14 16:27
 */
public class TestThrift {

	private static final Logger log = LoggerFactory.getLogger(TestThrift.class);

	/**
	 * 用于测试：thrift序列化和反序列化
	 */
	@Test
	public void test_thrift() throws TException {
		// 原始对象
		User user = new User("starbright");

		// thrift只认它自己的idl语言生成的对象
		com.starbright.thrift.User u = new com.starbright.thrift.User();
		u.setName(user.getName());

		// 序列化
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		TBinaryProtocol outBinaryProtocol = new TBinaryProtocol(new TIOStreamTransport(outputStream));
		u.write(outBinaryProtocol);
		byte[] bytes = outputStream.toByteArray();
		// 18 个字节
		log.info("bytes.length = {}", bytes.length);

		// 中间可以是网络传输操作

		// 反序列化
		ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
		TBinaryProtocol inputBinaryProtocol = new TBinaryProtocol(new TIOStreamTransport(inputStream));
		com.starbright.thrift.User newUser = new com.starbright.thrift.User();
		newUser.read(inputBinaryProtocol);
		log.info("newUser = {}", newUser);

		// 转换为自己系统的对象
		User user1 = new User();
		user1.setName(newUser.getName());
	}

	/**
	 * 用于测试：ThriftSerializer
	 */
	@SneakyThrows
	@Test
	public void test_ThriftSerializer() {
		com.starbright.User user = new com.starbright.User();
		user.setName("starbright");
		ThriftSerializer thriftSerializer = new ThriftSerializer();
		byte[] bytes = thriftSerializer.serialize(user);
		com.starbright.User newUser = (com.starbright.User) thriftSerializer.deserialize(bytes);
		Assert.assertEquals(user, newUser);
	}

}
