package com.starbright.protocol.impl;

import com.starbright.protocol.Protocol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 请求参数
 * @author: Star Bright
 * @date: 2024/9/24 10:51
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MethodInvokeData implements Protocol {

	/** 调用的接口 */
	private Class targetInterface;

	/** 调用的方法名称 */
	private String methodName;

	/** 参数类型 */
	private Class<?>[] parameterTypes;

	/** 参数 */
	private Object[] args;

}
