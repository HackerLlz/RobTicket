package com.duriamuk.robartifact.common.schedule;

import com.duriamuk.robartifact.common.constant.CookieContant;
import com.duriamuk.robartifact.common.constant.PrefixName;
import com.duriamuk.robartifact.common.exception.RobException;
import com.duriamuk.robartifact.common.messageQueue.MessageConsumerThreadPool;
import com.duriamuk.robartifact.common.messageQueue.MessageTask;
import com.duriamuk.robartifact.common.tool.*;
import com.duriamuk.robartifact.entity.DTO.robProcess.RobParamsDTO;
import com.duriamuk.robartifact.service.MailSendService;
import com.duriamuk.robartifact.service.RobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ScheduledFuture;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-03 18:46
 */
public class RobTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RobTask.class);
    private static final int MAX_ERROR_COUNT = 3;

    private RobService robService;

    private String payload;

    private String cookie;

    private ScheduledFuture<?> scheduledFuture;

    private Long period;

    private Long id;

    private Long userId;

    private int errorCount;

    public RobTask(String payload, long period, long id, long userId) {
        this.payload = payload;
        this.period = period;
        this.id = id;
        this.userId = userId;
        cookie = getCookie();
        robService = (RobService) ApplicationContextUtils.getBean("robService");
    }

    private String getCookie(){
       HttpServletRequest request = HttpUtils.getRequest();
       if (!ObjectUtils.isEmpty(request)) {
           return request.getHeader(HttpUtils.COOKIE);
       }
       return "";
    }

    @Override
    public void run() {
        if (isContinue()) {
            setCookieUamtk();
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

    private Boolean isContinue() {
        Object value = RedisUtils.get(PrefixName.TABLE_ROB_RECORD + id);
        return !ObjectUtils.isEmpty(value) ? true : false;
    }

    private void setCookieUamtk(){
        Object uamtk = RedisUtils.get(PrefixName.ROB_UAMTK + userId);
        if (!ObjectUtils.isEmpty(uamtk)) {
            String newCookie = CookieUtils.updateCookieStr(cookie, CookieContant.UAMTK, (String) uamtk);
            ThreadLocalUtils.set(newCookie);
        } else {
            RedisUtils.set(PrefixName.ROB_UAMTK + userId,
                    CookieUtils.getFromCookieStr(cookie, CookieContant.UAMTK));
            ThreadLocalUtils.set(cookie);
        }
    }

    private void doRob() {
        Boolean isRob = false;
        try {
            isRob = robService.doRob(payload);
        } catch (Exception e) {
            // 如空指针等错误会被Future截取，在这里捕获错误，并重置任务
            e.printStackTrace();
//            if (!(e instanceof RobException)) {
//                e.printStackTrace();
//            }
            resetTask(e);
        }
        if (isRob) {
            // 完成任务到取消任务之间尽量不能有别的操作，避免出错无法取消
            cancelTask();
            try {
                RedisUtils.setWithExpire(PrefixName.TABLE_ROB_RECORD + id, null, 0);
                RobParamsDTO robParamsDTO = new RobParamsDTO();
                robParamsDTO.setId(id);
                robParamsDTO.setStatus(0);
                robService.updateRobRecord(robParamsDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
            MessageConsumerThreadPool.message(new MessageTask(MailSendService.class, "sendMail", ""));
        }
    }

    private void resetTask(Exception e) {
        cookie = ThreadLocalUtils.get();
        cancelTask();
        // 到达最大错误数量或者抛出的是自定义终止错误则停止任务
        if (errorCount++ < MAX_ERROR_COUNT && !(e instanceof RobException)) {
            RobScheduledThreadPool.schedule(this);
        } else {
            logger.info("达到终止任务条件");
            RedisUtils.setWithExpire(PrefixName.TABLE_ROB_RECORD + id, null, 0);
        }
    }

    private void cancelTask() {
        boolean isCancel = false;
        int cancelTimes = 0;
        while (!isCancel) {
            if (scheduledFuture != null) {
                isCancel = scheduledFuture.cancel(false);
                logger.info("取消抢票任务失败 X {}", cancelTimes++);
            }
        }
        logger.info("取消抢票任务成功");
    }
}
