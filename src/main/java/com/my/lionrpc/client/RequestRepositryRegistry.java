package com.my.lionrpc.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.my.lionrpc.protocol.ResponseMessage;

public class RequestRepositryRegistry {

	private final static ConcurrentHashMap<String, AsyncInvokeFuture> repository = new ConcurrentHashMap<String, AsyncInvokeFuture>(1024);
	 
	static{
	 
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("RequestRepositryRegistry－TimeoutCheckTask");
				return t;
			}
		});
		executor.scheduleWithFixedDelay(new TimeoutCheckTask(), 5, 5, TimeUnit.SECONDS);
	}
	
	public static void register(AsyncInvokeFuture request){
		repository.put(request.getRequest().getRequestId(), request);
	}
	
	public static void callBack(ResponseMessage response){
		
		AsyncInvokeFuture  aync = repository.get(response.getRequestId());
		if(aync!=null){
			if(aync.getStatus().compareAndSet(0, 1)){
				aync.setResponse(response);
				aync.getLatch().countDown();
			}
		}
	}
	
	private static class TimeoutCheckTask implements Runnable{
		 
		public void run() {
			Iterator<String> iterator = repository.keySet().iterator();
			List<String> timeoutE = new ArrayList<String>();
			long now = System.currentTimeMillis();
			while(iterator.hasNext()){
				String rid = iterator.next();
				AsyncInvokeFuture f = repository.get(rid);
				if(f != null){
					if((now - f.getRequestTime())>10000){
						if(f.getStatus().compareAndSet(0, 408)){
							ResponseMessage rsp = new ResponseMessage();
							rsp.setRequestId(f.getRequest().getRequestId());
							rsp.setError("408");
							f.setResponse(rsp);
							f.getLatch().countDown();
							timeoutE.add(rid);
						}
					}
				}
			}
			for(String rid:timeoutE){
				repository.remove(rid);
			}
			
		}
	}
}
