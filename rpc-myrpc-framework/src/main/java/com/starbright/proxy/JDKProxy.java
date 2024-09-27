package com.starbright.proxy;

import com.starbright.cluster.Cluster;
import com.starbright.loadbalance.LoadBalancer;
import com.starbright.protocol.impl.MethodInvokeData;
import com.starbright.protocol.impl.Result;
import com.starbright.registry.HostAndPort;
import com.starbright.registry.Registry;
import com.starbright.transport.Transport;
import lombok.Data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @description: jdk 代理
 * @author: Star Bright
 * @date: 2024/9/26 16:09
 */
@Data
public class JDKProxy implements InvocationHandler {

	/**
	 * 远端调用 RPC 的接口，例如 UserService
	 */
	private final Class targetInterface;

	private Cluster cluster;

	private LoadBalancer loadBalancer;

	private Transport transport;

	private Registry registry;

	private List<HostAndPort> hostAndPorts;

	public JDKProxy(Class targetInterface) {
		this.targetInterface = targetInterface;
	}

	public Object createProxy() {
		// 1. 从注册中心中发现服务列表
		hostAndPorts = registry.getServiceList(targetInterface.getName());
		// 2. 服务列表的订阅
		registry.subscribeService(targetInterface.getName(), hostAndPorts);
		return Proxy.newProxyInstance(targetInterface.getClassLoader(), new Class[]{targetInterface}, this);
	}

	/**
	 * 通过 RPC 集群完成远端功能的调用
	 * Cluster 需要 LoadBalance、Transport、Register 获取服务列表、请求的参数 MethodInvokeData
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		MethodInvokeData methodInvokeData = new MethodInvokeData(targetInterface, method.getName(), method.getParameterTypes(), args);
		Result result = cluster.invoke(hostAndPorts, loadBalancer, transport, methodInvokeData);

		if (result.getException() != null) {
			throw result.getException();
		}

		return result.getResultValue();
	}

}
