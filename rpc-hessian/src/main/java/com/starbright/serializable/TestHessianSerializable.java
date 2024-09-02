package com.starbright.serializable;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.starbright.service.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @description: hessian的序列化方式测试
 * @author: Star Bright
 * @date: 2024/9/2 16:36
 */
public class TestHessianSerializable {

	private static final Logger log = LoggerFactory.getLogger(TestHessianSerializable.class);

	public static void main(String[] args) {
		String path = "/Users/baiyi/github/rpc-lession/rpc-hessian/test";
		encode(path);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		decode(path);
	}

	public static void encode(String path) {
		// hessian 序列化的目的 就是为了传输数据 基本类型、对象类型（需要实现Serializable接口）
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(path);
			Hessian2Output output = new Hessian2Output(fileOutputStream);
			output.writeObject(new User("starbright", "123456"));
			output.flush();
			fileOutputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void decode(String path) {
		// hessian反序列化
		try {
			FileInputStream inputStream = new FileInputStream(path);
			Hessian2Input input = new Hessian2Input(inputStream);
			User user = (User) input.readObject();
			log.debug("user: {}", user);
			input.close();
			inputStream.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
