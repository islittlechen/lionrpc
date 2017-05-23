package com.my.lionrpc.client.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

import javax.naming.OperationNotSupportedException;

import com.my.lionrpc.client.AsyncInvokeFuture;
import com.my.lionrpc.client.LionConnectionManager;
import com.my.lionrpc.client.RequestRepositryRegistry;
import com.my.lionrpc.common.exception.InvokeTimeOutException;
import com.my.lionrpc.common.exception.ServerInternalException;
import com.my.lionrpc.protocol.RequestMessage;
import com.my.lionrpc.protocol.ResponseMessage;

 
public class LionServiceProxy<T> implements InvocationHandler{
	 

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" +
                        Integer.toHexString(System.identityHashCode(proxy)) +
                        ", with InvocationHandler " + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }

        RequestMessage request = new RequestMessage();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);
        AsyncInvokeFuture future = new AsyncInvokeFuture();
        future.setRequest(request);
        RequestRepositryRegistry.register(future);
        LionConnectionManager.getInstance().getConnectionAndSendRequest(request);
        future.getLatch().await();
        ResponseMessage resp= future.getResponse();
        if("404".equals(resp.getError())){
        	throw new OperationNotSupportedException(method.getDeclaringClass().getName()+"."+method.getName());
        }else if("408".equals(resp.getError())){
        	throw new InvokeTimeOutException("time out");
        }else if("500".equals(resp.getError())){
        	throw new ServerInternalException("server 500");
        }else{
        	 if(!"0".equals(resp.getError())){
             	throw new RuntimeException("unkown exception");
             }
        }
         
        return resp.getResult();
    }
 

}
