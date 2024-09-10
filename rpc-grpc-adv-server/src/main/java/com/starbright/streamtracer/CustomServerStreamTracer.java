package com.starbright.streamtracer;

import io.grpc.ServerStreamTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 服务端流式拦截器
 * @author: Star Bright
 * @date: 2024/9/10 10:49
 */
public class CustomServerStreamTracer extends ServerStreamTracer {

	private static final Logger log = LoggerFactory.getLogger(CustomServerStreamTracer.class);

	// ====== inbound 拦截请求 ======


	@Override
	public void inboundMessage(int seqNo) {
		log.debug("server: 获得client发送消息的编号: {}", seqNo);
		super.inboundMessage(seqNo);
	}

	@Override
	public void inboundUncompressedSize(long bytes) {
		log.debug("server: 获得client未压缩消息的大小: {}", bytes);
		super.inboundUncompressedSize(bytes);
	}

	@Override
	public void inboundWireSize(long bytes) {
		log.debug("server: 获得client发送消息的大小: {}", bytes);
		super.inboundWireSize(bytes);
	}

	@Override
	public void inboundMessageRead(int seqNo, long optionalWireSize, long optionalUncompressedSize) {
		log.debug("server: 获得client发送的消息， seqNo: {}, optionalWireSize: {}, optionalUncompressedSize: {}", seqNo, optionalWireSize, optionalUncompressedSize);
		super.inboundMessageRead(seqNo, optionalWireSize, optionalUncompressedSize);
	}

	// ====== outbound 拦截响应 ======


	@Override
	public void outboundMessage(int seqNo) {
		log.debug("server: 响应消息的编号: {}", seqNo);
		super.outboundMessage(seqNo);
	}

	@Override
	public void outboundUncompressedSize(long bytes) {
		log.debug("server: 响应消息未压缩的大小: {}", bytes);
		super.outboundUncompressedSize(bytes);
	}

	@Override
	public void outboundWireSize(long bytes) {
		log.debug("server: 响应消息的大小: {}", bytes);
		super.outboundWireSize(bytes);
	}

	@Override
	public void outboundMessageSent(int seqNo, long optionalWireSize, long optionalUncompressedSize) {
		log.debug("server: 集中获得响应消息, {}, {}, {}", seqNo, optionalWireSize, optionalUncompressedSize);
		super.outboundMessageSent(seqNo, optionalWireSize, optionalUncompressedSize);
	}

}
