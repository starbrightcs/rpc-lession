package com.starbright;

import com.starbright.serializer.impl.JDKSerializer;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/14 14:02
 */
public class TestJDK {

	private static final Logger log = LoggerFactory.getLogger(TestJDK.class);

	/**
	 * 用于测试：测试jdk相关的序列化
	 * 序列化：Object --> 传输格式
	 * 反序列化：传输格式 --> Object
	 */
	@Test
	public void test_jdk() throws IOException, ClassNotFoundException {
		User user = new User("starbright");

		// 序列化 ByteArrayOutputStream <-- ObjectOutStream.write()  对象 --> 二进制格式，存储在本地内存的 byte[]
		// 二进制序列化本质就是 对象 --> byte[]
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
		// 这时候user存在ByteArrayOutputStream中
		objectOutputStream.writeObject(user);
		byte[] bytes = outputStream.toByteArray();
		// 81个字节
		log.info("bytes.length = {}", bytes.length);

		// 反序列化 byte[] --> User
		User newUser = (User) new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
		log.info("newUser = {}", newUser);

		// 序列化和反序列化的对象理应是相同的
		Assert.assertEquals(user, newUser);
	}

	/**
	 * 用于测试：jdk序列化封装测试
	 */
	@SneakyThrows
	@Test
	public void test_JDKSerializer() {
		User user = new User("starbright");
		JDKSerializer jdkSerializer = new JDKSerializer();
		byte[] bytes = jdkSerializer.serialize(user);
		User newUser = (User) jdkSerializer.deserialize(bytes);
		Assert.assertEquals(user, newUser);
	}

}
