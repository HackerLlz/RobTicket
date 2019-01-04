package com.example.frp.common.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-03 19:59
 */
public class RobScheduledThreadPool {
    private static final Logger logger = LoggerFactory.getLogger(RobScheduledThreadPool.class);

    private static ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) Executors
            .newScheduledThreadPool(3);

    public static ScheduledFuture<?> schedule(RobTask robTask, long period) {
        // 周期时间不受任务执行时间影响
        // scheduleAtFixedRate受执行时间影响
        ScheduledFuture<?> future = executor.scheduleWithFixedDelay(robTask, 0, period, TimeUnit.SECONDS);
        robTask.setScheduledFuture(future);
        return future;
    }
}