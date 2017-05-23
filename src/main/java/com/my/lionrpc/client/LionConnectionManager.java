package com.my.lionrpc.client;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.my.lionrpc.protocol.RequestMessage;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 
 * @author littlechen
 *
 */
public class LionConnectionManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(LionConnectionManager.class);
	private AtomicInteger threadCount = new AtomicInteger(1);
	
	private LinkedBlockingDeque<Channel> connectionPoll = new LinkedBlockingDeque<Channel>();
	
	private ConcurrentHashMap<String, Channel> connectionMap = new ConcurrentHashMap<String, Channel>();
	
	private LionConnectionManager() {}
	
	private static final LionConnectionManager instance = new LionConnectionManager();
	
 
	
	public static LionConnectionManager getInstance(){
		return instance;
	}
	
	public synchronized void connectServer(List<String> allServerAddress){
		int cpu = Runtime.getRuntime().availableProcessors();
		if(allServerAddress != null && allServerAddress.size() > 0){
			for(final String remotePeer:allServerAddress){
				Iterator<String> keyIter = connectionMap.keySet().iterator();
				boolean flag = false;
				while(keyIter.hasNext()){
					String key = keyIter.next();
					if(key.contains(remotePeer)){
						flag=true;
						break;
					}
				}
				if(flag)continue;
				for(int i = 1;i <= cpu ;i++){
					connectServerNode(remotePeer,i);
				}
			}
		}else{
			Iterator<String> iterator = connectionMap.keySet().iterator();
			while(iterator.hasNext()){
				String key = iterator.next();
				try{
					Channel channel = connectionMap.get(key);
					channel.close();
				}catch(Exception e){}
			}
			connectionMap.clear();
		}
		
	}
	
	public void getConnectionAndSendRequest(RequestMessage request){
		Channel channel = null;
		 try {
			channel = connectionPoll.take();
			while(channel != null){
				if(channel.isOpen()){
					channel.writeAndFlush(request);
					return;
				}else{
					channel = connectionPoll.take();
				}
			}
		} catch (Exception e) {
			LOGGER.error("getConnectionAndSendRequest happen exception.", e);
		}finally{
			if(channel != null && channel.isOpen()){
				connectionPoll.offer(channel);
			}
		}
	}
	
	
	public void connectServerNode(final String remotePeer,final int index){
		int cpu = Runtime.getRuntime().availableProcessors();
    	int loopCount = cpu;
		try{
			Bootstrap boot = new Bootstrap();
			if(Epoll.isAvailable()){
				boot.channel(EpollSocketChannel.class);
				boot.group(new EpollEventLoopGroup(loopCount,new ThreadFactory() {
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setName("EpollEventLoopGroupThread-"+threadCount.getAndIncrement());
						return t;
					}
				}));
			}else{
				boot.channel(NioSocketChannel.class);
				boot.group(new NioEventLoopGroup(loopCount,new ThreadFactory() {
					public Thread newThread(Runnable r) {
						Thread t = new Thread(r);
						t.setName("NioEventLoopGroup-"+threadCount.getAndIncrement());
						return t;
					}
				}));
			}
			boot.option(ChannelOption.TCP_NODELAY, true);
	        boot.handler(new LionClientChannelInitializer());
	        String[] ar = remotePeer.split(":");
	        InetSocketAddress remoteAddress = new InetSocketAddress(ar[0], Integer.parseInt(ar[1]));
	        ChannelFuture channelFuture = boot.connect(remoteAddress).sync();
	        channelFuture.addListener(new ChannelFutureListener() {
	            @Override
	            public void operationComplete(final ChannelFuture channelFuture) throws Exception {
	                if (channelFuture.isSuccess()) {
	                    LOGGER.debug("Successfully connect to remote server. remote peer = " + remotePeer);
	                    connectionPoll.offer(channelFuture.channel());
	                    connectionMap.put(remotePeer+"-"+index, channelFuture.channel());
	                }
	            }
	        });
		}catch(Exception e){
			LOGGER.error("connect to remote server. remote peer = " + remotePeer+" happen exception.", e);
	    }
	}
}
