package com.msb.mall.mallsearch.thread;

import java.util.concurrent.*;

public class ThreadDemo {

    // 定义一个线程池对象
    private static ExecutorService service = Executors.newFixedThreadPool(5);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main方法执行了...");
        ThreadDemo01 t1 = new ThreadDemo01();
        t1.start();

        ThreadDemo02 t2 = new ThreadDemo02();
        new Thread(t2).start();
        new Thread(()->{
            System.out.println("当前线程:" + Thread.currentThread().getName());
        }).start();

        // 通过Callable接口来实现  FutureTask 本质上是一个Runnable接口
        FutureTask futureTask = new FutureTask(new MyCallable());
        Thread t3 = new Thread(futureTask);
        t3.start();
        // 阻塞等待子线程的执行完成，然后获取线程的返回结果
        Object o = futureTask.get();
        System.out.println("o = " + o);

        service.execute(new Runnable() {
            @Override
            public void run() {
                System.out.println("线程池--》当前线程:" + Thread.currentThread().getName());
            }
        });

        System.out.println("main方法结束了...");
    }


}

class MyCallable implements Callable<Integer>{
    @Override
    public Integer call() throws Exception {
        System.out.println("当前线程:" + Thread.currentThread().getName());
        return 10;
    }
}

class ThreadDemo01 extends Thread{
    @Override
    public void run() {
        System.out.println("当前线程:" + Thread.currentThread().getName());
    }
}

class ThreadDemo02 implements Runnable{
    @Override
    public void run() {
        System.out.println("当前线程:" + Thread.currentThread().getName());
    }
}