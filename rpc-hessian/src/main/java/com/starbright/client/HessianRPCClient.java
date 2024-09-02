package com.starbright.client;

import com.caucho.hessian.client.HessianProxyFactory;
import com.starbright.service.User;
import com.starbright.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;

/**
 * @description: 模拟客户端
 * @author: Star Bright
 * @date: 2024/9/2 15:39
 */
public class HessianRPCClient {

	private static final Logger log = LoggerFactory.getLogger(HessianRPCClient.class);

	public static void main(String[] args) {
		HessianProxyFactory hessianProxyFactory = new HessianProxyFactory();

		// 创建远端RPC服务的代理对象
		try {
			UserService userService = (UserService) hessianProxyFactory.create(UserService.class, "http://localhost:8989/rpc-hessian/userServiceRPC");
			boolean login = userService.login("starbright", "123456");
			log.debug("login: {}", login);

			userService.register(new User("starbright", "123456"));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

}
