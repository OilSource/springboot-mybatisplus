package com.mybatis.plus.thread.callable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CallbaleTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        DemoThread demoThread1 = new DemoThread("A",6000l);
        DemoThread demoThread2 = new DemoThread("b",3000l);
        DemoThread demoThread3 = new DemoThread("c",4000l);
        List<DemoThread> threadList = new ArrayList<>();
        threadList.add(demoThread1);
        threadList.add(demoThread2);
        threadList.add(demoThread3);
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        List<Future<String>> futureList = new ArrayList<>();
        long start =System.currentTimeMillis();
        for(int i=0;i<threadList.size();i++){
            futureList.add(executorService.submit(threadList.get(i)));
        }
        List<String> result = new ArrayList<>();
        while(true){
            Iterator<Future<String>> iterator = futureList.iterator();
            while(iterator.hasNext()){
                Future<String> future =iterator.next();
                if(future.isDone()){
                    result.add(future.get());
                    iterator.remove();
                }
            }
            if(result.size()==3){
                break;
            }
        }
        System.out.println("执行所花费的时间："+(System.currentTimeMillis()-start));
        for(String theadName: result){
            System.out.println(theadName);
        }
    }
}
