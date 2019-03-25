package com.mybatis.plus.thread;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemoRunnable implements Runnable {

    private String name;

    public DemoRunnable(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        log.info("开执行线程"+name);
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("执行线程："+name+",完成！");
    }
}
