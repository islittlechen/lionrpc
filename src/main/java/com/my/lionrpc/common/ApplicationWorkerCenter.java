package com.my.lionrpc.common;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by littlechen on 17/5/21.
 */
public class ApplicationWorkerCenter{

    private ThreadPoolExecutor executorService;

    private AtomicInteger threadCount = new AtomicInteger(1);

    public ApplicationWorkerCenter(int coreSize,int maxSize,long keepAliveSecond,int queueSize){
        executorService = new ThreadPoolExecutor(coreSize, maxSize, keepAliveSecond, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(queueSize), new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("ApplicationWorker-"+threadCount);
                threadCount.incrementAndGet();
                return thread;
            }
        });
    }

    public void dispatchWorker(Runnable worker){
        executorService.submit(worker);
    }


}
