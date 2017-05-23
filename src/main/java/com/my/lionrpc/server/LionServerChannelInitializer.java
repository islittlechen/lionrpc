package com.my.lionrpc.server;

import com.my.lionrpc.common.ApplicationWorkerCenter;
import com.my.lionrpc.protocol.MessageDecoder;
import com.my.lionrpc.protocol.MessageEncoder;
import com.my.lionrpc.protocol.RequestMessage;
import com.my.lionrpc.protocol.ResponseMessage;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.util.Map;

/**
 * Created by littlechen on 17/5/21.
 */
public class LionServerChannelInitializer extends ChannelInitializer<SocketChannel>{

    private Map<String,Object> serverHandlerMap;

    private ApplicationWorkerCenter workerCenter;

    public LionServerChannelInitializer(Map<String,Object> serverHandlerMap,ApplicationWorkerCenter workerCenter){
        this.serverHandlerMap = serverHandlerMap;
        this.workerCenter = workerCenter;
    }


    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast(new LengthFieldBasedFrameDecoder(65536,0,4,0,0))
                .addLast(new MessageDecoder(RequestMessage.class))
                .addLast(new MessageEncoder(ResponseMessage.class))
                .addLast(new LionServerChannelInboundHanndler(serverHandlerMap,workerCenter));
    }
}
