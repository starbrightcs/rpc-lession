package com.starbright.serializer;

import com.starbright.protocol.Protocol;

/**
 * @description: 序列化器
 * @author: Star Bright
 * @date: 2024/9/14 19:22
 */
public interface Serializer {

	/**
	 * 序列化
	 *
	 * @param protocol 协议对象
	 * @return 字节数组
	 * @throws Exception 序列化异常
	 */
	byte[] serialize(Protocol protocol) throws Exception;

	/**
	 * 反序列化
	 *
	 * @param bytes 字节数组
	 * @return 协议对象
	 * @throws Exception 反序列化异常
	 */
	Protocol deserialize(byte[] bytes) throws Exception;

}
