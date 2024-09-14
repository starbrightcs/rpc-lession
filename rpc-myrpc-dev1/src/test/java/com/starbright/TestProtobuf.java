package com.starbright;

import com.google.protobuf.InvalidProtocolBufferException;
import com.starbright.serializer.impl.ProtoBufSerializer;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/14 16:53
 */
public class TestProtobuf {

	private static final Logger log = LoggerFactory.getLogger(TestProtobuf.class);

	/**
	 * 用于测试：protobuf序列化和反序列化
	 */
	@Test
	public void test_protobuf() throws InvalidProtocolBufferException {
		User user = new User("starbright");

		// protobuf 不认可我们创建的对象，只认可它的idl语言生成的对象
		HelloProto.HelloRequest helloRequest = HelloProto.HelloRequest.newBuilder().setName(user.getName()).build();

		// 序列化
		byte[] bytes = helloRequest.toByteArray();
		// 12个字节
		log.info("bytes.length = {}", bytes.length);

		// 反序列化
		HelloProto.HelloRequest parseFrom = HelloProto.HelloRequest.parseFrom(bytes);
		log.info("parseFrom = {}", parseFrom);
		Assert.assertEquals(user.getName(), parseFrom.getName());
	}

	/**
	 * 用于测试：ProtoBufSerializer
	 */
	@SneakyThrows
	@Test
	public void test_ProtobufSerializer() {
		User user = new User();
		user.setName("starbright");
		ProtoBufSerializer protobufSerializer = new ProtoBufSerializer();
		byte[] bytes = protobufSerializer.serialize(user);
		User newUser = (User) protobufSerializer.deserialize(bytes);
		Assert.assertEquals(user, newUser);
	}

}
