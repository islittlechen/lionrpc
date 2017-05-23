package com.my.lionrpc.server;

import java.lang.reflect.Method;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.my.lionrpc.protocol.RequestMessage;
import com.my.lionrpc.protocol.ResponseMessage;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

/**
 * Created by littlechen on 17/5/21.
 */
public class ServerWorker implements Runnable{

    private Map<String,Object> serverHandlerMap;
    private ChannelHandlerContext channelHandlerContext;
    private RequestMessage requestMessage;

    private static final Logger LOG = LoggerFactory.getLogger(ServerWorker.class);

    public ServerWorker(Map<String,Object> serverHandlerMap,ChannelHandlerContext channelHandlerContext,RequestMessage requestMessage){
        this.serverHandlerMap = serverHandlerMap;
        this.channelHandlerContext = channelHandlerContext;
        this.requestMessage = requestMessage;
    }

    public void run() {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setRequestId(requestMessage.getRequestId());
        try {
            String className = requestMessage.getClassName();
            Object serviceBean = serverHandlerMap.get(className);
            if(null == serviceBean){
                responseMessage.setError("404");
            }else {
                Class<?> serviceClass = serviceBean.getClass();
                String methodName = requestMessage.getMethodName();
                Class<?>[] parameterTypes = requestMessage.getParameterTypes();
                Object[] parameters = requestMessage.getParameters();
                // JDK reflect
                Method method = serviceClass.getMethod(methodName, parameterTypes);
                if (null == method) {
                    responseMessage.setError("404");
                } else {
                    method.setAccessible(true);
                    Object result = method.invoke(serviceBean, parameters);
                    responseMessage.setError("0");
                    responseMessage.setResult(result);
                }
            }
        }catch (Throwable t){
            LOG.error("ServerWorker call requestID="+requestMessage.getRequestId()+" happen exception.");
            responseMessage.setError("500");
        }

        channelHandlerContext.writeAndFlush(responseMessage).addListener(new ChannelFutureListener() {

            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if(channelFuture.isSuccess()==false) {
                    LOG.debug("Send response for requestID=" + requestMessage.getRequestId() + " failed.");
                }
            }
        });
    }
}
