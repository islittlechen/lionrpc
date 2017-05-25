package com.my.lionrpc.server;

import com.my.lionrpc.common.ApplicationWorkerCenter;
import com.my.lionrpc.protocol.RequestMessage;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by littlechen on 17/5/21.
 */
public class LionServerChannelInboundHanndler extends SimpleChannelInboundHandler<RequestMessage>{

    public static final Logger LOG = LoggerFactory.getLogger(LionServerChannelInboundHanndler.class);

    private Map<String,Object> serverHandlerMap;

    private ApplicationWorkerCenter workerCenter;

    public LionServerChannelInboundHanndler(Map<String,Object> serverHandlerMap, ApplicationWorkerCenter workerCenter){
        this.serverHandlerMap = serverHandlerMap;
        this.workerCenter = workerCenter;
    }

    protected void channelRead0(final ChannelHandlerContext channelHandlerContext, final RequestMessage requestMessage) throws Exception {
        workerCenter.dispatchWorker(new ServerWorker(serverHandlerMap,channelHandlerContext,requestMessage));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    	if(ctx.channel()!= null && ctx.channel().remoteAddress()!=null){
    		LOG.error("LionServerChannelInboundHanndler happen exception.remote address "+ctx.channel().remoteAddress().toString(),cause);
    	}
        if(ctx != null){
            ctx.close();
        }
    }
    
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
    	super.channelUnregistered(ctx);
    	if(ctx.channel()!= null && ctx.channel().remoteAddress()!=null){
    		LOG.error("LionServerChannelInboundHanndler channelUnregistered.remote address "+ctx.channel().remoteAddress().toString());
    	}
    }
}
