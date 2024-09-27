package com.starbright.protocol;

import java.io.Serializable;

/**
 * @description: 协议接口
 * @author: Star Bright
 * @date: 2024/9/24 10:48
 */
public interface Protocol extends Serializable {

	/**
	 * 魔数
	 */
	String MAGIC_NUM = "myRpc";

	/**
	 * 协议版本
	 */
	byte PROTOCOL_VERSION = 1;

}
