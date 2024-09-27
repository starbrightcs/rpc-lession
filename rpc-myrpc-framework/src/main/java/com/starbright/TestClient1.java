package com.starbright;

import com.starbright.protocol.impl.MethodInvokeData;
import com.starbright.protocol.impl.Result;
import com.starbright.registry.HostAndPort;
import com.starbright.service.UserService;
import com.starbright.transport.impl.NettyTransport;

import java.net.InetAddress;

/**
 * @description: 客户端调用测试
 * @author: Star Bright
 * @date: 2024/9/25 17:34
 */
public class TestClient1 {

	public static void main(String[] args) throws Exception {
		NettyTransport nettyTransport = new NettyTransport();
		HostAndPort hostAndPort = HostAndPort.builder()
				.hostName(InetAddress.getLocalHost().getHostName())
				.port(8081).build();
		MethodInvokeData methodInvokeData = MethodInvokeData.builder()
				.targetInterface(UserService.class)
				.methodName("login")
				.parameterTypes(new Class[]{String.class, String.class})
				.args(new Object[]{"starbright", "123456"})
				.build();
		Result result = nettyTransport.invoke(hostAndPort, methodInvokeData);
		System.out.println("result = " + result);
		nettyTransport.close();
	}

}
