package com.starbright;

import com.starbright.service.UserServiceImpl;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;

/**
 * @description: 服务端代码
 * @author: Star Bright
 * @date: 2024/9/3 12:00
 */
public class TestServer {

	public static void main(String[] args) throws TTransportException {
		// TTransport
		TServerSocket tServerSocket = new TServerSocket(9000);

		// TBinaryProtocol
		TBinaryProtocol.Factory factory = new TBinaryProtocol.Factory();

		// TProcessor 把对应的功能进行发布
		UserService.Processor<UserService.Iface> processor = new UserService.Processor<>(new UserServiceImpl());

		// 标准的构建者设计模式
		TServer.Args arg = new TSimpleServer.Args(tServerSocket).processor(processor).protocolFactory(factory);

		// TServer
		TSimpleServer server = new TSimpleServer(arg);
		server.serve();

	}

}
