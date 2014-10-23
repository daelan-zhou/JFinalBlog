package com.jfinal.ext.plugin.quartz;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 任务调度测试
 * @date 2013-11-22 上午10:44:25
 */
public class TestJob implements Job {
    static int callTime = 0;

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        callTime++;
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " TestJob works,callTime is: " + callTime);
    }
    
}
