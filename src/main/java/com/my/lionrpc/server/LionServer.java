package com.my.lionrpc.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.my.lionrpc.annotation.LionService;
import com.my.lionrpc.common.ApplicationWorkerCenter;
import com.my.lionrpc.registry.ServiceRegistry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * Created by littlechen on 17/5/21.
 */
public class LionServer implements ApplicationContextAware,InitializingBean{

    private Logger LOG = LoggerFactory.getLogger(LionServer.class);


    private String serverAddress;
    private ApplicationWorkerCenter workerCenter;
    private ServiceRegistry registry;
 

    private static AtomicInteger serverGroupCount = new AtomicInteger(1);
    private Map<String,Object> serverHandlerMap = new HashMap<String,Object>();
    
    

    public LionServer(String serverAddress, ApplicationWorkerCenter workerCenter, ServiceRegistry registry) {
		super();
		this.serverAddress = serverAddress;
		this.workerCenter = workerCenter;
		this.registry = registry;
	}

	public void afterPropertiesSet() throws Exception {
        ServerBootstrap boot = new ServerBootstrap();
        EventLoopGroup boss = null;
        EventLoopGroup worker = null;
        try {
        	int cpu = Runtime.getRuntime().availableProcessors();
        	int bossThreadNum = (cpu/4==0?1:cpu/4);
        	int workerThreadNum = cpu;
            Class<? extends ServerSocketChannel> clazz = null;
            if (Epoll.isAvailable()) {
                LOG.info("system use EpollServerSocketChannel");
                clazz = EpollServerSocketChannel.class;
                boss = new EpollEventLoopGroup(bossThreadNum, new EventLoopGroupThreadFactory("serverBossGroup" + serverGroupCount.get()));
                worker = new EpollEventLoopGroup(workerThreadNum, new EventLoopGroupThreadFactory("serverWorkerGroup" + serverGroupCount.getAndIncrement()));
            } else {
                LOG.info("system use NioServerSocketChannel");
                clazz = NioServerSocketChannel.class;
                boss = new NioEventLoopGroup(bossThreadNum, new EventLoopGroupThreadFactory("serverBossGroup" + serverGroupCount.get()));
                worker = new NioEventLoopGroup(workerThreadNum, new EventLoopGroupThreadFactory("serverWorkerGroup" + serverGroupCount.getAndIncrement()));
            }

            boot.group(boss,worker)
                    .channel(clazz)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .option(ChannelOption.SO_KEEPALIVE, false)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new LionServerChannelInitializer(this.serverHandlerMap,this.workerCenter));

            String[] array = serverAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            ChannelFuture future = boot.bind(host, port).sync();
            LOG.info("Server started on "+serverAddress);
            if (registry != null) {
            	registry.register(serverAddress);
            }

            future.channel().closeFuture().sync();
        }catch (Exception e){
            LOG.error("Server started happend exception.",e);
        }finally {
            if(boss != null){
                boss.shutdownGracefully();
            }
            if(worker != null){
                worker.shutdownGracefully();
            }
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String,Object> lionServiceMap = applicationContext.getBeansWithAnnotation(LionService.class);
        if(lionServiceMap != null){
            for (Object serviceBean : lionServiceMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(LionService.class).value().getName();
                serverHandlerMap.put(interfaceName, serviceBean);
            }
        }
    }
 
    public static class EventLoopGroupThreadFactory implements ThreadFactory {

        private String namePrefix;
        private AtomicInteger threadNum = new AtomicInteger(1);
        private ThreadGroup group;

        public EventLoopGroupThreadFactory(String namePrefix){
            this.namePrefix = namePrefix;
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + "_" + threadNum.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }

    }
}
