package com.starbright.serializer;

/**
 * @description: 序列化器
 * @author: Star Bright
 * @date: 2024/9/14 19:22
 */
public interface Serializer {

	/**
	 * 序列化
	 *
	 * @param obj 需要序列化的对象
	 * @return 字节数组
	 * @throws Exception 序列化异常
	 */
	byte[] serialize(Object obj) throws Exception;

	/**
	 * 反序列化
	 *
	 * @param bytes 字节数组
	 * @return 对象
	 * @throws Exception 反序列化异常
	 */
	Object deserialize(byte[] bytes) throws Exception;

}
