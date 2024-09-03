package com.starbright;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 客户端代码
 * @author: Star Bright
 * @date: 2024/9/3 12:10
 */
public class TestClient {

	private static final Logger log = LoggerFactory.getLogger(TestClient.class);

	public static void main(String[] args) throws TException {
		// TTransport
		TTransport tTransport = new TSocket("127.0.0.1", 9000);
		tTransport.open();

		// TProtocol
		TProtocol tProtocol = new TBinaryProtocol(tTransport);

		// 只需要传递协议即可，协议封装了网络通信
		UserService.Client client = new UserService.Client(tProtocol);
		User user = client.queryUserByNameAndPassword("starbright", "123456");
		log.info("user: {}", user);

	}

}
