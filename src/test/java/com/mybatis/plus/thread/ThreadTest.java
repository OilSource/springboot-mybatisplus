package com.mybatis.plus.thread;

public class ThreadTest {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(new DemoRunnable("xxxxxxxxxx"));
        thread.start();
        System.out.println("执行线程开始！");
        thread.join();
        System.out.println("执行线程结束！");
    }
}
