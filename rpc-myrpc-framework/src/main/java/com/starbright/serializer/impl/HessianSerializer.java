package com.starbright.serializer.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.starbright.protocol.Protocol;
import com.starbright.serializer.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @description: Hessian序列化
 * @author: Star Bright
 * @date: 2024/9/14 19:34
 */
public class HessianSerializer implements Serializer {

	@Override
	public byte[] serialize(Protocol protocol) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Hessian2Output hessian2Output = new Hessian2Output(outputStream);
		hessian2Output.writeObject(protocol);
		hessian2Output.flush();
		return outputStream.toByteArray();
	}

	@Override
	public Protocol deserialize(byte[] bytes) throws Exception {
		return (Protocol) new Hessian2Input(new ByteArrayInputStream(bytes)).readObject();
	}

}
