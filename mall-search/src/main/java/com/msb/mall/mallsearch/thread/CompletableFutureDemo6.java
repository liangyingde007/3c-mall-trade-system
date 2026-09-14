package com.msb.mall.mallsearch.thread;

import java.util.concurrent.*;

/**
 * CompletableFuture的介绍
 */
public class CompletableFutureDemo6 {

    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(5
            ,50
            ,10
            , TimeUnit.SECONDS
            ,new LinkedBlockingQueue<>(100)
            , Executors.defaultThreadFactory()
            ,new ThreadPoolExecutor.AbortPolicy()
    );

    /**
     * @param args
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {

        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1 线程开始了..." + Thread.currentThread().getName());
            int i = 100 / 5;
            System.out.println("任务1 线程结束了..." + Thread.currentThread().getName());
            return i;
        }, executor);
        CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2 线程开始了..." + Thread.currentThread().getName());
            int i = 100 /10;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("任务2 线程结束了..." + Thread.currentThread().getName());
            return i+"";
        }, executor);

        CompletableFuture<Object> future3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务3 线程开始了..." + Thread.currentThread().getName());
            int i = 100 /10;
            System.out.println("任务3 线程结束了..." + Thread.currentThread().getName());
            return i+"";
        }, executor);

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(future1, future2, future3);
        anyOf.get();
        System.out.println("主任务执行完成..." + anyOf.get());

        CompletableFuture<Void> allOf = CompletableFuture.allOf(future1, future2, future3);
        allOf.get();// 阻塞在这个位置，等待所有的任务执行完成
        System.out.println("主任务执行完成..." + future1.get() + " :" + future2.get() + " :" + future3.get());
    }
}
