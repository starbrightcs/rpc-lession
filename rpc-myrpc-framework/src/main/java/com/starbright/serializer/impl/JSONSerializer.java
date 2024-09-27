package com.starbright.serializer.impl;

import com.alibaba.fastjson.JSON;
import com.starbright.protocol.Protocol;
import com.starbright.serializer.Serializer;

import java.nio.charset.StandardCharsets;

/**
 * @description: fastjson 序列化器
 * @author: Star Bright
 * @date: 2024/9/14 20:23
 */
public class JSONSerializer implements Serializer {

	@Override
	public byte[] serialize(Protocol protocol) throws Exception {
		String json = JSON.toJSONString(protocol);
		return json.getBytes(StandardCharsets.UTF_8);
	}

	@Override
	public Protocol deserialize(byte[] bytes) throws Exception {
		return JSON.parseObject(bytes, Protocol.class);
	}

}
