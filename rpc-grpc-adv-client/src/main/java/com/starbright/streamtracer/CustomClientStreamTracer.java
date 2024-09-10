package com.starbright.streamtracer;

import io.grpc.ClientStreamTracer;
import io.grpc.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @description: 用于客户端流式拦截，既可以拦截请求也可以拦截响应
 * @author: Star Bright
 * @date: 2024/9/9 15:31
 */
public class CustomClientStreamTracer<ReqT, RespT> extends ClientStreamTracer {

	private static final Logger log = LoggerFactory.getLogger(CustomClientStreamTracer.class);

	/*
		站在客户端而言：
		outbound：请求相关的方法
		inputBound：响应相关的方法
		站在服务端而言：
		outbound：响应相关的方法
		inputBound：请求相关的方法
	 */

	// ============== 请求相关 ============

	@Override
	public void outboundHeaders() {
		log.debug("client: 请求头相关信息");
		super.outboundHeaders();
	}

	@Override
	public void outboundMessage(int seqNo) {
		log.debug("client: 设置消息编号 {}", seqNo);
		super.outboundMessage(seqNo);
	}

	@Override
	public void outboundUncompressedSize(long bytes) {
		log.debug("client: 获取未压缩消息大小: {}", bytes);
		super.outboundUncompressedSize(bytes);
	}


	@Override
	public void outboundWireSize(long bytes) {
		log.debug("client: 获得输出消息的大小: {}", bytes);
		super.outboundWireSize(bytes);
	}

	/**
	 * 发送消息的拦截
	 *
	 * @param seqNo                    流内消息的序列号，从 0 开始。它可用于与同一消息的outboundMessage(int)进行关联
	 * @param optionalWireSize         消息的线路大小。如果未知则为 -1
	 * @param optionalUncompressedSize 消息的未压缩序列化大小。如果未知则为 -1
	 */
	@Override
	public void outboundMessageSent(int seqNo, long optionalWireSize, long optionalUncompressedSize) {
		log.debug("client: 监控请求操作, [outboundMessageSent] seqNo: {}, optionalWireSize: {}, optionalUncompressedSize: {}", seqNo, optionalWireSize, optionalUncompressedSize);
		super.outboundMessageSent(seqNo, optionalWireSize, optionalUncompressedSize);
	}


	// ================ 响应相关 =============


	@Override
	public void inboundHeaders() {
		log.debug("client: 获得响应头信息");
		super.inboundHeaders();
	}

	@Override
	public void inboundMessage(int seqNo) {
		log.debug("client: 获得响应消息的编号: {}", seqNo);
		super.inboundMessage(seqNo);
	}

	@Override
	public void inboundUncompressedSize(long bytes) {
		log.debug("client: 获得未压缩消息大小: {}", bytes);
		super.inboundUncompressedSize(bytes);
	}

	@Override
	public void inboundWireSize(long bytes) {
		log.debug("client: 获得响应消息大小: {}", bytes);
		super.inboundWireSize(bytes);
	}

	@Override
	public void inboundMessageRead(int seqNo, long optionalWireSize, long optionalUncompressedSize) {
		log.debug("client, 集中获得响应消息的配置, 编号: {}, 响应消息的大小: {}, 未压缩消息的大小: {}", seqNo, optionalWireSize, optionalUncompressedSize);
		super.inboundMessageRead(seqNo, optionalWireSize, optionalUncompressedSize);
	}

	@Override
	public void inboundTrailers(Metadata trailers) {
		log.debug("client: 响应消息结束");
		super.inboundTrailers(trailers);
	}
}
