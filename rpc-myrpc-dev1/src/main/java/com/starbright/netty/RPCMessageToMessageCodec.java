package com.starbright.netty;

import com.starbright.User;
import com.starbright.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @description: 编解码器
 * @author: Star Bright
 * @date: 2024/9/15 17:03
 */
public class RPCMessageToMessageCodec extends MessageToMessageCodec<ByteBuf, User> {

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
		log.info("解码器运行...");

		// ByteBuf 转换为 byte[]
		// 跳过数据大小
		int protocolLength = byteBuf.readInt();
		byte[] bytes = new byte[protocolLength];
		byteBuf.readBytes(bytes);
		User u = (User) serializer.deserialize(bytes);

		// 解码器把封装转换的对象交给后续的操作
		list.add(u);
	}

	/**
	 * 作用：java object 通过序列化转换为 网络传输的格式
	 * 步骤：
	 * 1. java object 通过 msg 参数获得
	 * 2. 手动创建序列化器
	 * 3. byte[] 转换为 ByteBuf，然后通过 list 交给 Netty 进行传输处理
	 */
	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, User user, List<Object> list) throws Exception {
		log.info("编码器运行...");
		ByteBuf byteBuf = channelHandlerContext.alloc().buffer();

		byte[] bytes = serializer.serialize(user);

		// 处理半包粘包问题，告知封帧的解码器对应的数据大小
		byteBuf.writeInt(bytes.length);
		byteBuf.writeBytes(bytes);
		list.add(byteBuf);
	}

}
