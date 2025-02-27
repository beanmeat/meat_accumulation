package com.meat.distributedlock.scheduled;

import com.meat.distributedlock.annotation.DistributedLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author tchstart
 * @data 2025-02-27
 */
@Component
public class ScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @DistributedLock
    @Scheduled(cron = "0/5 * * * * ?")
    public void task(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
        logger.info("{},定时任务执行...",sdf.format(new Date()));
    }


}
