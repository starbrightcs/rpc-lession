package com.starbright.cluster.impl;

import com.starbright.cluster.Cluster;
import com.starbright.loadbalance.LoadBalancer;
import com.starbright.protocol.impl.MethodInvokeData;
import com.starbright.protocol.impl.Result;
import com.starbright.registry.HostAndPort;
import com.starbright.transport.Transport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @description: FailFast 容错，快速失败，直接抛出异常
 * @author: Star Bright
 * @date: 2024/9/26 15:53
 */
public class FailFastCluster implements Cluster {

	private static final Logger log = LoggerFactory.getLogger(FailFastCluster.class);

	@Override
	public Result invoke(List<HostAndPort> hostAndPorts, LoadBalancer loadBalancer, Transport transport, MethodInvokeData methodInvokeData) {
		HostAndPort hostAndPort = loadBalancer.select(hostAndPorts);
		try {
			Result result = transport.invoke(hostAndPort, methodInvokeData);
			transport.close();
			return result;
		} catch (Exception e) {
			transport.close();
			log.error("服务调用出现异常, 使用 FailFast 的方式进行容错, e: {}", e.getMessage(), e);
			throw new RuntimeException("服务调用出现异常" + e);
		}
	}

}
