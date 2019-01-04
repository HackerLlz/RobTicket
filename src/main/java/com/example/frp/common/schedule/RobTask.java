package com.example.frp.common.schedule;

import com.example.frp.common.tool.ApplicationContextUtils;
import com.example.frp.common.tool.HttpUtils;
import com.example.frp.common.tool.ThreadLocalUtils;
import com.example.frp.service.RobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-03 18:46
 */
public class RobTask implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(RobTask.class);

    private String payload;

    private String cookie;

    private RobService robService = (RobService)ApplicationContextUtils.getBean("robService");

    private ScheduledFuture<?> scheduledFuture;

    public RobTask(String payload) {
        this.payload = payload;
        cookie = HttpUtils.getRequest().getHeader("cookie");
    }

    @Override
    public void run() {
        ThreadLocalUtils.set(cookie);
        Boolean isRob = robService.doRob(payload);
        if (isRob) {
            cancelTask();
        }
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    private void cancelTask() {
        boolean isCancel = false;
        int cancelTimes = 0;
        while (!isCancel) {
            if (scheduledFuture != null) {
                isCancel = scheduledFuture.cancel(true);
                logger.info("取消抢票任务失败 X {}", ++cancelTimes);
            }
        }
        logger.info("取消抢票任务成功");
    }
}
