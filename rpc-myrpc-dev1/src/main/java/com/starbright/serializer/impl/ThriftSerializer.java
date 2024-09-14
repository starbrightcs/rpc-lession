package com.starbright.serializer.impl;

import com.starbright.serializer.Serializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TIOStreamTransport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @description: thrift 序列化器，需要注意的是idl生成的对象【自动生成的】
 * @author: Star Bright
 * @date: 2024/9/14 19:38
 */
public class ThriftSerializer implements Serializer {

	// 此处的obj不是实际框架的类型，避免了接口的污染，不会出现实际框架对应的类型
	@Override
	public byte[] serialize(Object obj) throws Exception {
		com.starbright.User user = (com.starbright.User) obj;

		// 数据拷贝,这里先手工拷贝，后续考虑Spring的BeanUtil等工具
		com.starbright.thrift.User u = new com.starbright.thrift.User();
		u.setName(user.getName());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		TBinaryProtocol tBinaryProtocol = new TBinaryProtocol(new TIOStreamTransport(outputStream));
		u.write(tBinaryProtocol);
		return outputStream.toByteArray();
	}

	@Override
	public Object deserialize(byte[] bytes) throws Exception {
		TBinaryProtocol tBinaryProtocol = new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(bytes)));
		com.starbright.thrift.User user = new com.starbright.thrift.User();
		user.read(tBinaryProtocol);
		com.starbright.User u = new com.starbright.User();
		u.setName(user.getName());
		return u;
	}

}
