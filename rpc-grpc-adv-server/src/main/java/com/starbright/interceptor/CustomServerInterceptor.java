package com.starbright.interceptor;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 普通服务端拦截器
 * @author: Star Bright
 * @date: 2024/9/9 12:02
 */
public class CustomServerInterceptor implements ServerInterceptor {

	private static final Logger log = LoggerFactory.getLogger(CustomServerInterceptor.class);

	/**
	 * 为什么返回的是 SeverCall.Listener？因为针对于服务端来说，一直都是对请求的拦截
	 */
	@Override
	public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
		// 在服务端，拦截请求操作的功能，写在这个方法中
		log.debug("服务器端拦截器生效");
		// 默认的返回的ServerCall.Listener仅仅能够完成请求数据的监听，没有拦截的功能
		// 所以要做扩展，使用装饰器模式。
		// return serverCallHandler.startCall(serverCall, metadata);

		// 自定义复杂拦截器
		// return new CustomServerCallListener<>(serverCallHandler.startCall(serverCall, metadata));


		// 1. 包装ServerCall，处理服务端响应拦截
		CustomServerCall<ReqT, RespT> customServerCall = new CustomServerCall<>(serverCall);
		// 2. 包装listener，处理服务端请求拦截
		return new CustomServerCallListener<>(serverCallHandler.startCall(customServerCall, metadata));
	}

}
