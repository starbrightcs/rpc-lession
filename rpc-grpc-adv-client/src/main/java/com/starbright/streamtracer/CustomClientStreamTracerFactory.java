package com.starbright.streamtracer;

import io.grpc.ClientStreamTracer;
import io.grpc.Metadata;

/**
 * @description: 通过工厂创建自定义的流式拦截器
 * @author: Star Bright
 * @date: 2024/9/9 15:52
 */
public class CustomClientStreamTracerFactory  extends ClientStreamTracer.Factory {

	/**
	 * 为新的客户端流创建ClientStreamTracer 。在传输创建流时，会调用此方法
	 */
	@Override
	public ClientStreamTracer newClientStreamTracer(ClientStreamTracer.StreamInfo info, Metadata headers) {
		return new CustomClientStreamTracer<>();
	}

}
