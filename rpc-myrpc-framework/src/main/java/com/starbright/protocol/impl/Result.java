package com.starbright.protocol.impl;

import com.starbright.protocol.Protocol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 响应数据
 * @author: Star Bright
 * @date: 2024/9/24 10:53
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Result implements Protocol {

	/** 响应数据 */
	private Object resultValue;

	/** 异常信息 */
	private Exception exception;

}
