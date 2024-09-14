package com.starbright;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.starbright.serializer.impl.HessianSerializer;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/14 14:31
 */
public class TestHessian {

	private static final Logger log = LoggerFactory.getLogger(TestHessian.class);

	/**
	 * 用于测试：hessian序列化测试
	 */
	@Test
	public void test_hessian() throws IOException {
		User user = new User("starbright");

		// 序列化 User --> byte[]
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Hessian2Output hessian2Output = new Hessian2Output(outputStream);
		hessian2Output.writeObject(user);
		hessian2Output.flush();
		byte[] bytes = outputStream.toByteArray();
		// 39个字节
		log.info("bytes.length = {}", bytes.length);

		// 反序列化 byte[] --> User
		User newUser = (User) new Hessian2Input(new ByteArrayInputStream(bytes)).readObject();

		// 序列化和反序列化对象理应相等
		Assert.assertEquals(user, newUser);
	}


	/**
	 * 用于测试：HessianSerializer测试
	 */
	@SneakyThrows
	@Test
	public void test_HessianSerializer() {
		User user = new User("starbright");
		HessianSerializer hessianSerializer = new HessianSerializer();
		byte[] bytes = hessianSerializer.serialize(user);
		User newUser = (User) hessianSerializer.deserialize(bytes);
		Assert.assertEquals(user, newUser);
	}

}
