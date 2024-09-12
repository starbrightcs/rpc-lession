package com.starbright.resolver;

import io.grpc.NameResolver;
import io.grpc.NameResolverProvider;

import java.net.URI;

/**
 * @description: 自定义 NameResolverProvider
 * @author: Star Bright
 * @date: 2024/9/12 11:22
 */
public class CustomNameResolverProvider extends NameResolverProvider {

	/**
	 * NameResolverProvider是否可用
	 *
	 * @return 是否可用
	 */
	@Override
	protected boolean isAvailable() {
		return true;
	}

	/**
	 * NameResolverProvider的优先级，需要注意要比Dns的高，dns的优先级为5
	 *
	 * @return NameResolverProvider的优先级
	 */
	@Override
	protected int priority() {
		return 6;
	}

	/**
	 * 生成nameResolver，核心
	 */
	@Override
	public NameResolver newNameResolver(URI targetUri, NameResolver.Args args) {
		return new CustomNameResolver(targetUri.toString().replace("consul:///", ""));
	}

	/**
	 * 名字解析（注册中心）通信的协议 consul
	 *
	 * @return 名字解析（注册中心）通信的协议
	 */
	@Override
	public String getDefaultScheme() {
		return "consul";
	}
}
