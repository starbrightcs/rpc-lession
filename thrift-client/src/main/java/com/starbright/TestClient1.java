package com.starbright;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.layered.TFramedTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/3 17:13
 */
public class TestClient1 {

	private static final Logger log = LoggerFactory.getLogger(TestClient1.class);

	public static void main(String[] args) throws TException {
		// TTransport 完成和服务端的网络连接
		TSocket tSocket = new TSocket("localhost", 9000);
		TFramedTransport tFramedTransport = new TFramedTransport(tSocket);
		tSocket.open();

		// 协议 TProtocol
		TCompactProtocol tCompactProtocol = new TCompactProtocol(tFramedTransport);

		// 创建代理 stub 存根 桩
		UserService.Client userService = new UserService.Client(tCompactProtocol);

		User user = userService.queryUserByNameAndPassword("starbright", "123456");
		log.info("user: {}", user);
	}

}
