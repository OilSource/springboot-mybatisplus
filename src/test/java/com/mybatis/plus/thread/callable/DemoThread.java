package com.mybatis.plus.thread.callable;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
@Slf4j
public class DemoThread implements Callable<String> {
    private String name;

    private Long time;

    public DemoThread(String name,Long time) {
        this.name = name;
        this.time = time;
    }

    @Override
    public String call() throws Exception {
        log.info("开执行线程"+name);
        Thread.sleep(time);
        log.info("执行线程："+name+",完成！");
        return name;
    }
}
