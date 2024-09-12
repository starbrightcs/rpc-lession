package com.starbright.rpcgrpcbootserver.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 自定义拦截器
 * @author: Star Bright
 * @date: 2024/9/12 15:34
 */ 
@GrpcGlobalServerInterceptor
public class CustomBootServerInterceptor implements ServerInterceptor {

	private static final Logger log = LoggerFactory.getLogger(CustomBootServerInterceptor.class);

	@Override
	public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
		log.info("服务端拦截器起作用了...");
		return next.startCall(call, headers);
	}


}
