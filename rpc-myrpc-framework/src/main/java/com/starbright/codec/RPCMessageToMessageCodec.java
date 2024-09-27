package com.starbright.codec;

import com.starbright.protocol.Protocol;
import com.starbright.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @description: 编解码器
 * @author: Star Bright
 * @date: 2024/9/15 17:03
 */
public class RPCMessageToMessageCodec extends MessageToMessageCodec<ByteBuf, Protocol> {

	private static final Logger log = LoggerFactory.getLogger(RPCMessageToMessageCodec.class);

	private final Serializer serializer;


	public RPCMessageToMessageCodec(Serializer serializer) {
		this.serializer = serializer;
	}

	/**
	 * 作用：网络传输的格式 通过反序列化转换为 java object
	 * 1. 网络传输的格式，netty 传输的就是 ByteBuf
	 * 2. 手动创建序列化器
	 * 3. 转换为 java object
	 */
	@Override
	protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
		log.info("解码器运行, 当前编解码器为: {}", serializer.getClass().getSimpleName());

		// 获取魔数进行对比，防止被篡改
		CharSequence charSequence = byteBuf.readCharSequence(5, StandardCharsets.UTF_8);
		if (!Protocol.MAGIC_NUM.contentEquals(charSequence)) {
			log.error("当前魔数: {} 和原始魔数: {} 不同", charSequence, Protocol.MAGIC_NUM);
			throw new RuntimeException("MAGIC NUM ERROR!");
		}

		// 版本信息
		byte protocolVersion = byteBuf.readByte();

		// ByteBuf 转换为 byte[]
		int protocolLength = byteBuf.readInt();
		byte[] bytes = new byte[protocolLength];
		byteBuf.readBytes(bytes);
		Protocol protocol = serializer.deserialize(bytes);

		// 解码器把封装转换的对象交给后续的操作
		list.add(protocol);
	}

	/**
	 * 作用：java object 通过序列化转换为 网络传输的格式
	 * 步骤：
	 * 1. java object 通过 msg 参数获得
	 * 2. 手动创建序列化器
	 * 3. byte[] 转换为 ByteBuf，然后通过 list 交给 Netty 进行传输处理
	 */
	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, Protocol protocol, List<Object> list) throws Exception {
		log.info("编码器运行, 当前编解码器为: {}", serializer.getClass().getSimpleName());
		ByteBuf byteBuf = channelHandlerContext.alloc().buffer();

		// 1. 魔数/幻术，占用 5 个字节空间
		byteBuf.writeBytes(Protocol.MAGIC_NUM.getBytes(StandardCharsets.UTF_8));

		// 2. 协议版本，1 个字节
		byteBuf.writeByte(Protocol.PROTOCOL_VERSION);

		// 3. 序列化数据
		byte[] bytes = serializer.serialize(protocol);

		// 处理半包粘包问题，告知封帧的解码器对应的数据大小
		byteBuf.writeInt(bytes.length);
		byteBuf.writeBytes(bytes);
		list.add(byteBuf);
	}

}
