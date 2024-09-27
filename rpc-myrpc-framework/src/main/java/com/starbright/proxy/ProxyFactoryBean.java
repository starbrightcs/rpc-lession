package com.starbright.proxy;

import com.starbright.cluster.Cluster;
import com.starbright.loadbalance.LoadBalancer;
import com.starbright.protocol.impl.MethodInvokeData;
import com.starbright.protocol.impl.Result;
import com.starbright.registry.HostAndPort;
import com.starbright.registry.Registry;
import com.starbright.transport.Transport;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/27 12:24
 */
@Data
public class ProxyFactoryBean implements FactoryBean, InvocationHandler {

	private Class targetInterface;
	private Cluster cluster;
	private Transport transport;
	private Registry registry;
	private LoadBalancer loadBalancer;
	private List<HostAndPort> hostAndPorts;

	public ProxyFactoryBean(Class targetInterface) {
		this.targetInterface = targetInterface;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		MethodInvokeData methodInvokeData = new MethodInvokeData(targetInterface, method.getName(), method.getParameterTypes(), args);
		Result result = cluster.invoke(hostAndPorts, loadBalancer, transport, methodInvokeData);

		if (result.getException() != null) {
			throw result.getException();
		}

		return result.getResultValue();
	}

	/**
	 * 代理创建的代码 就可以写在这个位置
	 */
	@Override
	public Object getObject() throws Exception {
		// 1 从注册中心中发现服务列表
		hostAndPorts = registry.getServiceList(targetInterface.getName());
		// 2 服务列表的订阅
		registry.subscribeService(targetInterface.getName(), hostAndPorts);
		return Proxy.newProxyInstance(targetInterface.getClassLoader(), new Class[]{targetInterface}, this);
	}

	@Override
	public Class<?> getObjectType() {
		return targetInterface;
	}

}

