package com.starbright;

import com.alibaba.fastjson.JSON;
import com.caucho.hessian.io.Hessian2Output;
import com.starbright.serializer.impl.JSONSerializer;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/14 18:10
 */
public class TestJSON {

	/**
	 * 用于测试：json
	 */
	@Test
	public void test_json() {
		User user = new User("starbright");

		// 序列化
		String userJSON = JSON.toJSONString(user);
		byte[] bytes = userJSON.getBytes(StandardCharsets.UTF_8);
		// 21个字节
		System.out.println("bytes.length = " + bytes.length);

		// 反序列化
		User newUser = JSON.parseObject(userJSON, User.class);
		Assert.assertEquals(user, newUser);
	}


	/**
	 * 用于测试：json 和 hessian 对比
	 */
	@Test
	public void test_json_hessian() throws IOException {
		User user = new User("starbright", "11111", 18, "广州市", "1111111", "1111@qq.com");

		// json 序列化
		String userJSON = JSON.toJSONString(user);
		byte[] jsonBytes = userJSON.getBytes(StandardCharsets.UTF_8);
		// 109 字节
		System.out.println("json bytes.length = " + jsonBytes.length);

		// hessian 序列化
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Hessian2Output hessian2Output = new Hessian2Output(outputStream);
		hessian2Output.writeObject(user);
		hessian2Output.flush();
		byte[] hessianBytes = outputStream.toByteArray();
		// 107 字节
		System.out.println("hessian bytes.length = " + hessianBytes.length);
	}

	/**
	 * 用于测试： JSONSerializer
	 */
	@SneakyThrows
	@Test
	public void test_JSONSerializer() {
		User user = new User("starbright");
		JSONSerializer jsonSerializer = new JSONSerializer();
		byte[] bytes = jsonSerializer.serialize(user);
		User newUser = (User) jsonSerializer.deserialize(bytes);
		Assert.assertEquals(user, newUser);
	}

}
