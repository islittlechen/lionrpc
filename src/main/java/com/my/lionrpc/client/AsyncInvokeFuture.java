package com.my.lionrpc.client;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import com.my.lionrpc.protocol.RequestMessage;
import com.my.lionrpc.protocol.ResponseMessage;

public class AsyncInvokeFuture {
	
	private long requestTime = System.currentTimeMillis();
	
	private RequestMessage request;
	
	private ResponseMessage response;
	
	private CountDownLatch latch = new CountDownLatch(1);
	
	private AtomicInteger status = new AtomicInteger(0); 

	public RequestMessage getRequest() {
		return request;
	}

	public void setRequest(RequestMessage request) {
		this.request = request;
	}

	public ResponseMessage getResponse() {
		return response;
	}

	public void setResponse(ResponseMessage response) {
		this.response = response;
	}
	
	
	public CountDownLatch getLatch() {
		return latch;
	}
	
	public long getRequestTime() {
		return requestTime;
	}

	public AtomicInteger getStatus() {
		return status;
	}
 
	
	
}
