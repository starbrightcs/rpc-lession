package com.starbright.loadbalance;

import com.starbright.registry.HostAndPort;

import java.util.List;

/**
 * @description: 负载均衡
 * @author: Star Bright
 * @date: 2024/9/25 17:57
 */
public interface LoadBalancer {

	HostAndPort select(List<HostAndPort> hostAndPorts);

}
