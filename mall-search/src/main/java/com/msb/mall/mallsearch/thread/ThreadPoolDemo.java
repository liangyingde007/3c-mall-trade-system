package com.msb.mall.mallsearch.thread;

import java.util.concurrent.*;

public class ThreadPoolDemo {

    /**
     * 线程池详解
     * @param args
     */
    public static void main(String[] args) {
        // 第一种获取的方式
        ExecutorService service = Executors.newFixedThreadPool(10);
        // Executors.newCachedThreadPool()
        // Executors.newScheduledThreadPool() 定时任务的线程池
        // Executors.newSingleThreadExecutor(); // 线程池中永远只有一个线程去处理，并发的情况下会被变为同步的处理

        // 第二种方式： 直接new ThreadPoolExecutor()对象，并且手动的指定对应的参数
        // corePoolSize:线程池的核心线程数量 线程池创建出来后就会 new Thread() 5个
        // maximumPoolSize:最大的线程数量，线程池支持的最大的线程数
        // keepAliveTime:存活时间，当线程数大于核心线程，空闲的线程的存活时间 8-5=3
        // unit:存活时间的单位
        // BlockingQueue<Runnable> workQueue:阻塞队列 当线程数超过了核心线程数据，那么新的请求到来的时候会加入到阻塞的队列中
        // new LinkedBlockingQueue<>() 默认队列的长度是 Integer.MAX 那这个就太大了，所以我们需要指定队列的长度
        // threadFactory:创建线程的工厂对象
        // RejectedExecutionHandler handler:当线程数大于最大线程数的时候会执行的淘汰策略
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5
                , 100
                , 10
                , TimeUnit.SECONDS
                , new LinkedBlockingQueue<>(10000)
                , Executors.defaultThreadFactory()
                , new ThreadPoolExecutor.AbortPolicy()
        );
        poolExecutor.execute(()->{
            System.out.println("----->" + Thread.currentThread().getName());
        });


    }
}
