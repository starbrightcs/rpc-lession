package com.starbright.streamtracer;

import io.grpc.Metadata;
import io.grpc.ServerStreamTracer;

/**
 * @description: 工厂创建对象
 * @author: Star Bright
 * @date: 2024/9/10 10:48
 */
public class CustomServerStreamTracerFactory extends ServerStreamTracer.Factory {
	@Override
	public ServerStreamTracer newServerStreamTracer(String fullMethodName, Metadata headers) {
		return new CustomServerStreamTracer();
	}
}
