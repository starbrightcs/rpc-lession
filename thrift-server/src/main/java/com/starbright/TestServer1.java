package com.starbright;

import com.starbright.service.UserServiceImpl;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.layered.TFramedTransport;

/**
 * @description:
 * @author: Star Bright
 * @date: 2024/9/3 17:17
 */
public class TestServer1 {

	public static void main(String[] args) throws TTransportException {
		TNonblockingServerSocket tNonblockingServerSocket = new TNonblockingServerSocket(9000);

		// TTransport 网络通信
		TFramedTransport.Factory tFramedTransport = new TFramedTransport.Factory();

		// TProtocol 协议
		TCompactProtocol.Factory tCompactProtocol = new TCompactProtocol.Factory();

		// TProcessor 业务处理，把通信数据和业务功能整合在一起
		UserService.Processor<UserServiceImpl> userServiceProcessor = new UserService.Processor<>(new UserServiceImpl());

		// TServer
		TThreadedSelectorServer.Args arg = new TThreadedSelectorServer
				.Args(tNonblockingServerSocket)
				.processor(userServiceProcessor)
				.transportFactory(tFramedTransport)
				.protocolFactory(tCompactProtocol);

		TThreadedSelectorServer server = new TThreadedSelectorServer(arg);
		server.serve();

	}

}
