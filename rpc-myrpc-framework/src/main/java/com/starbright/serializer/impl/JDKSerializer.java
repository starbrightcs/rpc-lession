package com.starbright.serializer.impl;

import com.starbright.protocol.Protocol;
import com.starbright.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @description: JDK 序列化器
 * @author: Star Bright
 * @date: 2024/9/14 19:29
 */
public class JDKSerializer implements Serializer {

	@Override
	public byte[] serialize(Protocol protocol) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		new ObjectOutputStream(outputStream).writeObject(protocol);
		return outputStream.toByteArray();
	}

	@Override
	public Protocol deserialize(byte[] bytes) throws Exception {
		return (Protocol) new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
	}

}
