package com.starbright.cluster;

import com.starbright.loadbalance.LoadBalancer;
import com.starbright.protocol.impl.MethodInvokeData;
import com.starbright.protocol.impl.Result;
import com.starbright.registry.HostAndPort;
import com.starbright.transport.Transport;

import java.util.List;

/**
 * @description: client 集群接口
 * @author: Star Bright
 * @date: 2024/9/25 18:18
 */
public interface Cluster {

	/**
	 * 接口调用
	 *
	 * @param hostAndPorts     服务列表信息
	 * @param loadBalancer     负载均衡
	 * @param transport        网络通信
	 * @param methodInvokeData 请求参数
	 * @return 结果
	 */
	Result invoke(List<HostAndPort> hostAndPorts, LoadBalancer loadBalancer, Transport transport, MethodInvokeData methodInvokeData);

}
