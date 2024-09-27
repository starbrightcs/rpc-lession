package com.starbright.cluster.impl;

import com.starbright.cluster.Cluster;
import com.starbright.loadbalance.LoadBalancer;
import com.starbright.protocol.impl.MethodInvokeData;
import com.starbright.protocol.impl.Result;
import com.starbright.registry.HostAndPort;
import com.starbright.transport.Transport;
import com.starbright.transport.impl.NettyTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/26 16:06
 */
public class FailOverCluster implements Cluster {

	private static final Logger log = LoggerFactory.getLogger(FailOverCluster.class);

	@Override
	public Result invoke(List<HostAndPort> hostAndPorts, LoadBalancer loadBalancer, Transport transport, MethodInvokeData methodInvokeData) {
		HostAndPort hostAndPort = loadBalancer.select(hostAndPorts);

		log.info("访问的IP {} ", hostAndPort.getPort());
		Result result = null;
		try {
			result = transport.invoke(hostAndPort, methodInvokeData);
			transport.close();
		} catch (Exception e) {
			log.error("集群调用产生错误 使用FailOver容错 ", e);
			transport.close();
            /*
               上一步hostAndPort出问题，从List取其他的HostAndPort进行访问。
             */
			hostAndPorts.remove(hostAndPort);
			if (hostAndPorts.isEmpty()) {
				throw new RuntimeException("集群出现错误....");
			} else {
				return invoke(hostAndPorts, loadBalancer, new NettyTransport(), methodInvokeData);
			}
		}
		return result;
	}
}
