package com.example.frp.common.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.frp.common.tool.*;
import com.example.frp.service.RobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-03 18:46
 */
public class RobTask implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(RobTask.class);
    private static final List<String> seatTypeGD = Arrays.asList("O", "M", "9");
    private static final List<String> seatTypeListOther = Arrays.asList("1", "3", "4");

    private String payload;

    private String cookie;

    private String trainNumber;

    private RobService robService = (RobService)ApplicationContextUtils.getBean("robService");

    private ScheduledFuture<?> scheduledFuture;

    private Long period;

    private String seatType;

    private Integer seatTypeNum = 0;

    private Boolean firstFlag = true;

    private Boolean isAllSeatType = false;

    private List<String> seatTypeList;

    public RobTask(String payload, long period) {
        this.payload = payload;
        this.period = period;
        cookie = HttpUtils.getRequest().getHeader("cookie");
    }

    @Override
    public void run(){
        checkSeatType();
        changSeatType();
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

    private void checkSeatType() {
        if (firstFlag) {
            JSONObject jsonObject = JSON.parseObject(payload);
            seatType = jsonObject.getString("passengerTicketStr").substring(0, 3);
            JSONObject robRequestData = jsonObject.getJSONObject("robRequestData");
            trainNumber = robRequestData.getString("trainNumber");
            if ("all".equals(seatType)) {
                firstFlag = false;
                isAllSeatType = true;
                chooseSeatTypeList();
            }
        }
    }

    private void chooseSeatTypeList() {
        if (trainNumber.contains("G") || trainNumber.contains("D")) {
            seatTypeList = seatTypeGD;
        } else {
            seatTypeList = seatTypeListOther;
        }
    }

    private void changSeatType() {
        if (isAllSeatType) {
            seatType = seatTypeList.get(seatTypeNum ++);
            payload = payload.replaceFirst(StrUtils.findVlaue("passengerTicketStr", "\":\"", 0, ",", payload), seatType);
            if (seatTypeNum > 2) {
                seatTypeNum = 0;
            }
            logger.info("已改变席别，编号：{}", seatType);
        }
    }

    private void doRob() {
        logger.info("抢票车次：{}", trainNumber);
        Boolean isRob = false;
        try{
            isRob = robService.doRob(payload);
        }catch (Exception e) {
            // 如空指针等错误会被Future截取，在这里捕获错误，并重置任务
            e.printStackTrace();
            resetTask();
        }
        if (isRob) {
            cancelTask();
            sendMail();
        }
    }

    private  void resetTask() {
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

    private void sendMail(){
        try {
            MailSendUtils.sendHtmlMessage("1174827250@qq.com", "抢票成功", "恭喜您！抢票成功");
        } catch (MessagingException e) {
            logger.info("邮件发送失败：{}", e.getMessage());
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            logger.info("邮件发送失败：{}", e.getMessage());
            e.printStackTrace();
        }
    }
}
