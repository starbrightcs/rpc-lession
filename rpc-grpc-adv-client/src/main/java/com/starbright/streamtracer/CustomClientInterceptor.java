package com.starbright.streamtracer;

import io.grpc.*;

/**
 * @description: 拦截器
 * @author: Star Bright
 * @date: 2024/9/9 15:30
 */
public class CustomClientInterceptor implements ClientInterceptor {

	@Override
	public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
		// 把自己开发的StreamTracerFactory融入到gRPC体系中，怎么融入？通过CallOptions
		callOptions = callOptions.withStreamTracerFactory(new CustomClientStreamTracerFactory());
		return next.newCall(method, callOptions);
	}

}
