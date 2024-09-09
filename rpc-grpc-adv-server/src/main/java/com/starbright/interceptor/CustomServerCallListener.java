package com.starbright.interceptor;

import io.grpc.ForwardingServerCallListener;
import io.grpc.ServerCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 自定义复杂服务端拦截器
 * @author: Star Bright
 * @date: 2024/9/9 12:17
 */
public class CustomServerCallListener<ReqT> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {

	private static final Logger log = LoggerFactory.getLogger(CustomServerCallListener.class);

	public CustomServerCallListener(ServerCall.Listener<ReqT> delegate) {
		super(delegate);
	}

	/**
	 * 准备接收请求数据
	 */
	@Override
	public void onReady() {
		log.debug("onReady method invoked...");
		super.onReady();
	}

	/**
	 * 接收请求数据
	 *
	 * @param message 请求数据
	 */
	@Override
	public void onMessage(ReqT message) {
		log.debug("接收到了请求提交的数据, message: {}", message);
		super.onMessage(message);
	}

	@Override
	public void onHalfClose() {
		log.debug("监听到了半连接阶段...");
		super.onHalfClose();
	}

	/**
	 * 在服务端调用next.OnComplete()方法时会调用该方法
	 */
	@Override
	public void onComplete() {
		log.debug("服务端调用onComplete方法");
		super.onComplete();
	}

	@Override
	public void onCancel() {
		log.debug("出现异常后，会调用该方法，可以用于关闭资源等功能");
		super.onCancel();
	}

}
