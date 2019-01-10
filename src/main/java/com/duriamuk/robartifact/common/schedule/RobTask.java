package com.duriamuk.robartifact.common.schedule;

import com.duriamuk.robartifact.common.tool.ApplicationContextUtils;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.common.tool.ThreadLocalUtils;
import com.duriamuk.robartifact.service.MailSendService;
import com.duriamuk.robartifact.service.RobService;
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

    private RobService robService = (RobService) ApplicationContextUtils.getBean("robService");

    private MailSendService mailSendService = (MailSendService) ApplicationContextUtils.getBean("mailSendService");

    private String payload;

    private String cookie;

    private ScheduledFuture<?> scheduledFuture;

    private Long period;

    public RobTask(String payload, long period) {
        this.payload = payload;
        this.period = period;
        cookie = HttpUtils.getRequest().getHeader("cookie");
    }

    @Override
    public void run(){
        ThreadLocalUtils.set(cookie);
        doRob();
        cookie = ThreadLocalUtils.get();
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public Long getPeriod() {
        return period;
    }

    private void doRob() {
        Boolean isRob = false;
        try{
            isRob = robService.doRob(payload);
        }catch (Exception e) {
            // 如空指针等错误会被Future截取，在这里捕获错误，并重置任务
            e.printStackTrace();
            resetTask();
        }
        if (isRob) {
            // 完成任务到取消任务之间不能有别的操作，避免出错无法取消
            cancelTask();
            mailSendService.sendMail();
        }
    }

    private void resetTask() {
        cookie = ThreadLocalUtils.get();
        cancelTask();
        RobScheduledThreadPool.schedule(this);
    }

    private void cancelTask() {
        boolean isCancel = false;
        int cancelTimes = 0;
        while (!isCancel) {
            if (scheduledFuture != null) {
                isCancel = scheduledFuture.cancel(false);
                logger.info("取消抢票任务失败 X {}", cancelTimes ++);
            }
        }
        logger.info("取消抢票任务成功");
    }
}
