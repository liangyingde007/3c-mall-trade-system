package com.msb.mall.mallsearch.thread;

import java.util.concurrent.*;

/**
 * CompletableFuture的介绍
 */
public class CompletableFutureDemo {

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(5
            ,50
            ,10
            , TimeUnit.SECONDS
            ,new LinkedBlockingQueue<>(100)
            , Executors.defaultThreadFactory()
            ,new ThreadPoolExecutor.AbortPolicy()
    );

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        System.out.println("main -- 线程开始了...");
        // 获取CompletableFuture对象
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.runAsync(() -> {
            System.out.println("线程开始了...");
            int i = 100/50;
            System.out.println("线程结束了...");
        },executor);
        System.out.println("main -- 线程结束了...");

        System.out.println("------------");
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("线程开始了...");
            int i = 100 / 50;
            System.out.println("线程结束了...");
            return i;
        }, executor);
        // 可以处理异步任务之后的操作
        System.out.println("获取的线程的返回结果是：" + future.get() );
    }
}
