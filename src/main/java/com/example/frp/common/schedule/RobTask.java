package com.example.frp.common.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.frp.common.tool.ApplicationContextUtils;
import com.example.frp.common.tool.HttpUtils;
import com.example.frp.common.tool.StrUtils;
import com.example.frp.common.tool.ThreadLocalUtils;
import com.example.frp.service.RobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public void run() {
        checkSeatType();
        changSeatType();
        ThreadLocalUtils.set(cookie);
        logger.info("抢票车次：{}", trainNumber);
        Boolean isRob = false;
        try{
            isRob = robService.doRob(payload);
        }catch (Exception e) {
            e.printStackTrace();
            cancelTask();
            RobScheduledThreadPool.schedule(this);
        }
        if (isRob) {
            cancelTask();
        }
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
            seatType = jsonObject.get("passengerTicketStr").toString().substring(0, 3);
            JSONObject robRequestData = JSON.parseObject(JSON.toJSONString(jsonObject.get("robRequestData")));
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
            logger.info("改变席别，编号：{}", seatType);
        }
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
