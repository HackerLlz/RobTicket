package com.duriamuk.robartifact.common.schedule;

import com.duriamuk.robartifact.common.constant.TableName;
import com.duriamuk.robartifact.common.messageQueue.MessageConsumerThreadPool;
import com.duriamuk.robartifact.common.messageQueue.MessageTask;
import com.duriamuk.robartifact.common.tool.ApplicationContextUtils;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.common.tool.RedisUtils;
import com.duriamuk.robartifact.common.tool.ThreadLocalUtils;
import com.duriamuk.robartifact.service.MailSendService;
import com.duriamuk.robartifact.service.RobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.ScheduledFuture;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-03 18:46
 */
public class RobTask implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(RobTask.class);
    private static final int MAX_ERROR_COUNT = 10;

    private RobService robService;

    private String payload;

    private String cookie;

    private ScheduledFuture<?> scheduledFuture;

    private Long period;

    private Long id;

    private int errorCount;

    public RobTask(String payload, long period, long id) {
        this.payload = payload;
        this.period = period;
        this.id = id;
        cookie = HttpUtils.getRequest().getHeader(HttpUtils.COOKIE);
        robService = (RobService) ApplicationContextUtils.getBean("robService");
    }

    @Override
    public void run(){
        if (isContinue()) {
            ThreadLocalUtils.set(cookie);
            doRob();
            cookie = ThreadLocalUtils.get();
        } else {
            cancelTask();
        }
    }

    public void setScheduledFuture(ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public Long getPeriod() {
        return period;
    }

    private Boolean isContinue(){
        Object value = RedisUtils.get(TableName.ROB_RECORD + id);
        if (!ObjectUtils.isEmpty(value)) {
            return true;
        }
        return false;
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
            try {
                RedisUtils.setWithExpire(TableName.ROB_RECORD + id, null, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MessageConsumerThreadPool.message(new MessageTask(MailSendService.class, "sendMail", ""));
        }
    }

    private void resetTask() {
        cookie = ThreadLocalUtils.get();
        cancelTask();
        if (errorCount ++ < MAX_ERROR_COUNT) {
            RobScheduledThreadPool.schedule(this);
        } else {
            RedisUtils.setWithExpire(TableName.ROB_RECORD + id, null, 0);
        }
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
