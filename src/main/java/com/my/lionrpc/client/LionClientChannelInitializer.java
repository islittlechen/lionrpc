package com.my.lionrpc.client;

import com.my.lionrpc.protocol.MessageDecoder;
import com.my.lionrpc.protocol.MessageEncoder;
import com.my.lionrpc.protocol.RequestMessage;
import com.my.lionrpc.protocol.ResponseMessage;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Created by littlechen on 17/5/21.
 */
public class LionClientChannelInitializer extends ChannelInitializer<SocketChannel>{


    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
        		.addLast(new MessageEncoder(RequestMessage.class))
        		.addLast(new LengthFieldBasedFrameDecoder(65536,0,4,0,0))
                .addLast(new MessageDecoder(ResponseMessage.class))
                .addLast(new LionClientChannelInboundHandler());

    }
}
