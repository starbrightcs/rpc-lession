package com.starbright.discovery;

import java.util.List;

/**
 * @description: 服务发现接口
 * @author: Star Bright
 * @date: 2024/9/22 10:49
 */
public interface ServiceDiscovery {

	/**
	 * 根据服务类别获取可用的服务节点列表
	 *
	 * @param serviceName 服务类别，例如 /OrderService、/UserService
	 * @return 服务节点列表
	 */
	List<String> discover(String serviceName);


	/**
	 * 监听服务类别的变化，不需要每一次都去获取
	 *
	 * @param serviceName 服务类别，例如 / OrderService、/ UserService
	 */
	void registerWatch(String serviceName);

}
