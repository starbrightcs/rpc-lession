package com.starbright.discovery;

import java.util.List;

/**
 * @description: 负载均衡算法
 * @author: Star Bright
 * @date: 2024/9/22 11:06
 */
public interface LoadBalance {

	String select(List<String> urls);

}
