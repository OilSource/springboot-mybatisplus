package com.mybatis.plus.configuration;

import com.mybatis.plus.job.DataSyncJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
public class ScheduledConfiguration implements SchedulingConfigurer {

    public static AtomicInteger num = new AtomicInteger(0);

    private  String cron="0/4 * * * * ?";

    private String name="测试";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(doTask(), getTrigger());
    }

    /**
     * 业务执行方法
     * @return
     */
    private Runnable doTask() {
        return new DataSyncJob();
    }

    /**
     * 业务触发器
     * @return
     */
    private Trigger getTrigger() {
        return (triggerContext) ->{
            // 触发器
            log.info("执行trigger");
            Date date = triggerContext.lastActualExecutionTime();
            if(null == date){
                return new Date(System.currentTimeMillis()+1000L);
            }
            if(date.getTime() - System.currentTimeMillis()>3000){
                return new Date(date.getTime() +100L);
            }
            return new Date(date.getTime() +2000L);
        };
    }
}
