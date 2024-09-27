package com.starbright.transport;

import com.starbright.protocol.impl.MethodInvokeData;
import com.starbright.protocol.impl.Result;
import com.starbright.registry.HostAndPort;

/**
 * @description: 网络通信
 * @author: Star Bright
 * @date: 2024/9/25 17:14
 */
public interface Transport {

	/**
	 * 方法调用【远程调用】
	 * @param hostAndPort 调用服务的ip:port
	 * @param methodInvokeData 调用接口信息
	 * @return 响应结果
	 */
	Result invoke(HostAndPort hostAndPort, MethodInvokeData methodInvokeData) throws Exception;

	void close();

}
