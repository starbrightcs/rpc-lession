package com.starbright.registry;

import java.util.List;

/**
 * @description: 封装注册中心功能
 * <p/>
 * 注册的服务格式
 * <blockquote><pre>
 *     /rpc
 *       /com.starbright.xxxService
 *          /provider
 *              /ip:port
 * </pre></blockquote><p>
 * @author: Star Bright
 * @date: 2024/9/24 14:15
 */
public interface Registry {

	/** 服务前缀 */
	final String SERVICE_PREFIX = "/rpc/";

	/** 服务后缀 */
	final String SERVICE_SUFFIX = "/provider";

	/**
	 * 服务注册
	 *
	 * @param targetInterfaceName 接口名称
	 * @param hostAndPort         ip:port 信息
	 */
	void register(String targetInterfaceName, HostAndPort hostAndPort);

	/**
	 * 服务发现【获取服务类别】
	 *
	 * @param targetInterfaceName 接口名称
	 * @return 服务列表
	 */
	List<HostAndPort> getServiceList(String targetInterfaceName);


	/**
	 * 服务的订阅【移除非健康的节点信息】
	 *
	 * @param targetInterfaceName 接口名称
	 * @param existHostAndPorts   已存在的服务列表
	 */
	void subscribeService(String targetInterfaceName, List<HostAndPort> existHostAndPorts);

}
