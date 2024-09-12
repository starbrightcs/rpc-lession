package com.starbright.rpcgrpcbootclient.interceptor;

import io.grpc.*;
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 自定义拦截器
 * @author: Star Bright
 * @date: 2024/9/12 15:19
 */
@GrpcGlobalClientInterceptor
public class CustomBootInterceptor implements ClientInterceptor {

	private static final Logger log = LoggerFactory.getLogger(CustomBootInterceptor.class);

	@Override
	public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method, CallOptions callOptions, Channel next) {
		log.info("自定义拦截器起作用了...");

		return next.newCall(method, callOptions);
	}

}
