package com.starbright.serializer.impl;

import com.starbright.HelloProto;
import com.starbright.User;
import com.starbright.serializer.Serializer;

/**
 * @description: protobuf 序列化器
 * @author: Star Bright
 * @date: 2024/9/14 20:05
 */
public class ProtoBufSerializer implements Serializer {

	@Override
	public byte[] serialize(Object obj) throws Exception {
		com.starbright.User user = (com.starbright.User) obj;

		HelloProto.HelloRequest request = HelloProto.HelloRequest.newBuilder().setName(user.getName()).build();
		return request.toByteArray();
	}

	@Override
	public Object deserialize(byte[] bytes) throws Exception {
		HelloProto.HelloRequest request = HelloProto.HelloRequest.parseFrom(bytes);
		User user = new User();
		user.setName(request.getName());
		return user;
	}

}
