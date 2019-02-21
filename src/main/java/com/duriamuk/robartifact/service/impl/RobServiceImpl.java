package com.duriamuk.robartifact.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.constant.PrefixName;
import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.common.tool.RedisUtils;
import com.duriamuk.robartifact.common.tool.StrUtils;
import com.duriamuk.robartifact.entity.DTO.orderSubmitParams.OrderRequestDTO;
import com.duriamuk.robartifact.entity.DTO.orderSubmitParams.TicketInfoDTO;
import com.duriamuk.robartifact.entity.DTO.robProcess.*;
import com.duriamuk.robartifact.mapper.RobMapper;
import com.duriamuk.robartifact.service.LoginService;
import com.duriamuk.robartifact.service.PassengerService;
import com.duriamuk.robartifact.service.TicketService;
import com.duriamuk.robartifact.service.RobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.util.ListUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-02 17:55
 */
@Service("robService")
public class RobServiceImpl implements RobService {
    private static final Logger logger = LoggerFactory.getLogger(RobServiceImpl.class);
    private static final List<String> seatTypeAll = Arrays.asList("O", "M", "9", "1", "3", "4");
    private static final List<String> seatTypeGD = Arrays.asList("O", "M", "9");
    private static final List<String> seatTypeOther = Arrays.asList("1", "3", "4");
    private static final int LEFT_TIME_INDEX = 10;
    private static final int SHORT_TRAIN_NUM_INDEX = 3;
    private static final int LONG_TRAIN_NUM_INDEX = 2;
    private static final int SECRET_INDEX = 0;
    private static final int MAX_ROB_TASK = 2;

    @Autowired
    private TicketService ticketService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private RobMapper robMapper;

    @Override
    public Boolean insertRobRecord(RobParamsDTO robParamsDTO) {
        List<RobParamsDTO> robParamsDTOList = listRobRecordByUserId(robParamsDTO.getUserId());
        int goingRobTaskCount = countGoingRobTask(robParamsDTOList);
        if (goingRobTaskCount <= MAX_ROB_TASK) {
            robMapper.insertRobRecord(robParamsDTO);
            return true;
        }
        return false;
    }

    private int countGoingRobTask(List<RobParamsDTO> robParamsDTOList) {
        int count = 0;
        for (RobParamsDTO rob : robParamsDTOList) {
            Object redisObj = RedisUtils.get(PrefixName.TABLE_ROB_RECORD + rob.getId());
            if (!ObjectUtils.isEmpty(redisObj)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public List<RobParamsDTO> listRobRecordByUserId(Long userId) {
        return robMapper.listRobRecordByUserId(userId);
    }

    @Override
    public RobParamsDTO getRobRecordById(Long id) {
        return robMapper.getRobRecordById(id);
    }

    @Override
    public void deleteRobRecordById(Long id) {
        robMapper.deleteRobRecordById(id);
    }

    @Override
    public void updateRobRecord(RobParamsDTO robParamsDTO) {
        robMapper.updateRobRecord(robParamsDTO);
    }

    public Boolean doRob(String payload) {
        logger.info("尝试抢票，入参：{}", payload);
        boolean isLogin = loginService.keepLogin();
        if (isLogin) {
            JSONObject jsonObject = JSON.parseObject(payload);
            CheckOrderDTO checkOrderDTO = JSON.parseObject(jsonObject.getString("checkOrderData"), CheckOrderDTO.class);
            QueueCountDTO queueCountDTO = JSON.parseObject(jsonObject.getString("queueCountData"), QueueCountDTO.class);
            DoOrderDTO doOrderDTO = JSON.parseObject(jsonObject.getString("doOrderData"), DoOrderDTO.class);
            RobParamsDTO robParamsDTO = JSON.parseObject(jsonObject.getString("robParamsData"), RobParamsDTO.class);

            boolean isAllSeatType = checkAllSeatType(checkOrderDTO);
            if (isAllSeatType) {
                List<String> seatTypeList = chooseSeatTypeList(robParamsDTO);
                for (String seatType : seatTypeList) {
                    // 席别轮询太快会被12306拒绝，要间隔一秒以上
                    changeSeatType(seatType, checkOrderDTO, queueCountDTO, doOrderDTO);
                    boolean isSuccess = checkAllDateSecretStr(robParamsDTO, checkOrderDTO, queueCountDTO, doOrderDTO);
                    if (isSuccess) {
                        return true;
                    }
                }
            } else {
                boolean isSuccess = checkAllDateSecretStr(robParamsDTO, checkOrderDTO, queueCountDTO, doOrderDTO);
                if (isSuccess) {
                    return true;
                }
            }
        }
        logger.info("抢票失败");
        return false;
    }

    /*---------------------------------------------------START: 抢票流程------------------------------------------------------*/
    private Boolean checkAllDateSecretStr(RobParamsDTO robParamsDTO, CheckOrderDTO checkOrderDTO,
                                          QueueCountDTO queueCountDTO, DoOrderDTO doOrderDTO) {
        for (String trainDate : robParamsDTO.getTrainDate().split(",")) {
            List<String> secretStrList = getSecretStrListInOneDay(robParamsDTO, trainDate);
            if (!ListUtils.isEmpty(secretStrList)) {
                for (String secretStr : secretStrList) {
                    boolean isReserve = doReserve(secretStr, checkOrderDTO, queueCountDTO, robParamsDTO, doOrderDTO);
                    if (isReserve) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private List<String> getSecretStrListInOneDay(RobParamsDTO robParamsDTO, String trainDate) {
        robParamsDTO.setTrainDate(trainDate);
        String queryResult = ticketService.listTicket(buildQueryData(robParamsDTO));
        if (queryResult.startsWith("{")) {
            JSONArray tickets = JSON.parseObject(queryResult).getJSONObject("data").getJSONArray("result");
            List<String> secretStrList = buildSecretStrList(robParamsDTO, tickets);
            if (ListUtils.isEmpty(secretStrList)) {
                logger.info("未获得secretStrList");
                return null;
            }
            logger.info("已获得secretStrList");
            return secretStrList;
        }
        logger.info("查询车票失败");
        return null;
    }

    private Boolean doReserve(String secretStr, CheckOrderDTO checkOrderDTO, QueueCountDTO queueCountDTO,
                              RobParamsDTO robParamsDTO, DoOrderDTO doOrderDTO) {
        boolean isRequestSubmit = submitOrderRequest(buildOrderRequestData(robParamsDTO, secretStr));
        if (isRequestSubmit) {
            Map<String, String> orderParamsMap = getOrderParamsMap();
            if (orderParamsMap != null) {
                boolean isSubmit = submitOrder(checkOrderDTO, queueCountDTO, robParamsDTO,
                        doOrderDTO, orderParamsMap);
                if (isSubmit) {
                    return true;
                }
            }
        }
        return false;
    }

    private Boolean submitOrderRequest(String data) {
        String result = ticketService.submitOrderRequest(data);
        if (result.startsWith("{") && "[]".equals(JSON.parseObject(result).getString("messages"))) {
            return true;
        }
        logger.info("提交订单请求失败");
        return false;
    }

    private Map<String, String> getOrderParamsMap() {
        logger.info("获取订单参数");
        String url = UrlConstant.OTN_URL + "confirmPassenger/initDc";
        String result = HttpUtils.doPostForm(url, null, true);
        Map<String, String> map = buildOrderParamsMap(result);
        if (map == null) {
            logger.info("订单参数获取失败");
        }
        return map;
    }

    private Boolean submitOrder(CheckOrderDTO checkOrderDTO, QueueCountDTO queueCountDTO,
                                RobParamsDTO robParamsDTO, DoOrderDTO doOrderDTO,
                                Map<String, String> orderParamsMap) {
        OrderRequestDTO orderRequestDTO = JSON.parseObject(orderParamsMap.get("orderRequestDTO"), OrderRequestDTO.class);
        TicketInfoDTO ticketInfoDTO = JSON.parseObject(orderParamsMap.get("ticketInfoForPassengerForm"), TicketInfoDTO.class);
        String globalRepeatSubmitToken = orderParamsMap.get("globalRepeatSubmitToken");

        boolean isCheck = checkOrder(buildCheckOrderData(checkOrderDTO, ticketInfoDTO, globalRepeatSubmitToken));
        if (isCheck) {
            boolean robNoSeat = robParamsDTO.getRobNoSeat();
            if (!robNoSeat) {
                boolean isSeatAvailable = checkSeatAvailable(buildQueueCountData(queueCountDTO, orderRequestDTO, ticketInfoDTO, globalRepeatSubmitToken));
                if (!isSeatAvailable) {
                    return false;
                }
            }
//            boolean isOrder = doOrder(buildDoOrderData(doOrderDTO, ticketInfoDTO));
//            if (isOrder) {
//                logger.info("抢票成功");
//                return true;
//            }
        }
        return false;
    }

    private boolean checkOrder(String checkOrderData) {
        String result = passengerService.checkOrderInfo(checkOrderData);
        if (result.startsWith("{")) {
            JSONObject jsonObject = JSON.parseObject(result).getJSONObject("data");
            if (jsonObject.getBoolean("submitStatus")) {
                return true;
            } else {
                logger.info("验证订单信息失败：{}", jsonObject.getString("errMsg"));
                return false;
            }
        }
        logger.info("验证订单信息失败");
        return false;
    }

    private boolean doOrder(String data) {
        String url = "confirmPassenger/confirmSingleForQueue";
        String result = passengerService.doOrder(url, data);
        if (result.startsWith("{")) {
            JSONObject jsonObject = JSON.parseObject(result).getJSONObject("data");
            if (jsonObject.getBoolean("submitStatus")) {
                return true;
            } else {
                logger.info("确认下单失败：{}", jsonObject.getString("messages"));
                return false;
            }
        }
        logger.info("确认下单失败");
        return false;
    }
    /*---------------------------------------------------END: 抢票流程--------------------------------------------------------*/

    /*---------------------------------------------------START: 抢票选项------------------------------------------------------*/
    private List<String> buildSecretStrList(RobParamsDTO robParamsDTO, JSONArray tickets) {
        // 根据有无指定车次来初始化List大小，加快算法速度
        List<String> secretStrList = null;
        if (!StringUtils.isEmpty(robParamsDTO.getTrainNumber())) {
            secretStrList = new ArrayList<>();
        } else {
            secretStrList = new ArrayList<>(tickets.size());
        }
        filterQueryResult(robParamsDTO, tickets, secretStrList);
        return secretStrList;
    }

    private void filterQueryResult(RobParamsDTO robParamsDTO, JSONArray tickets, List<String> secretStrList) {
        String[] trainNumbers = robParamsDTO.getTrainNumber().split(",");
        for (Object ticket : tickets) {
            String[] attrs = ticket.toString().split("\\|");
            filterQueryResultBytrainDate(robParamsDTO, trainNumbers, attrs, secretStrList);
        }
    }

    private void filterQueryResultBytrainDate(RobParamsDTO robParamsDTO, String[] trainNumbers,
                                              String[] attrs, List<String> secretStrList) {
        String leftTime = attrs[LEFT_TIME_INDEX];
        if (compareTime(leftTime, robParamsDTO.getLeftTimeBegin()) &&
                !compareTime(leftTime, robParamsDTO.getLeftTimeEnd())) {
            filterQueryResultBytrainNumbers(trainNumbers, attrs, secretStrList);
        }
    }

    private void filterQueryResultBytrainNumbers(String[] trainNumbers, String[] attrs, List<String> secretStrList) {
        String shortNum = attrs[SHORT_TRAIN_NUM_INDEX];
        String longNum = attrs[LONG_TRAIN_NUM_INDEX];
        for (String no : trainNumbers) {
            if (shortNum.equals(no) || longNum.equals(no)) {
                String secretStr = attrs[SECRET_INDEX];
                if (!StringUtils.isEmpty(secretStr)) {
                    secretStrList.add(secretStr);
                    logger.info("添加secretStr成功，车次：{}", no);
                    return;
                }
            }
        }
    }

    private Boolean compareTime(String first, String second) {
        String[] firsts = first.split(":");
        String[] seconds = second.split(":");
        if (Integer.parseInt(firsts[0]) > Integer.parseInt(seconds[0]) ||
                (Integer.parseInt(firsts[0]) == Integer.parseInt(seconds[0]) &&
                        Integer.parseInt(firsts[1]) >= Integer.parseInt(seconds[1]))) {
            return true;
        }
        return false;
    }

    private Boolean checkAllSeatType(CheckOrderDTO checkOrderDTO) {
        String[] passengers = checkOrderDTO.getPassengerTicketStr().split("_");
        for (String str : passengers) {
            if (str.startsWith("all")) {
                return true;
            }
        }
        return false;
    }

    private List<String> chooseSeatTypeList(RobParamsDTO robParamsDTO) {
        String trainNumber = robParamsDTO.getTrainNumber();
        if (StringUtils.isEmpty(trainNumber)) {
            return seatTypeAll;
        }
        if (trainNumber.contains("G") || trainNumber.contains("D")) {
            if (trainNumber.contains("K") || trainNumber.contains("T") || trainNumber.contains("Z")) {
                return seatTypeAll;
            }
            return seatTypeGD;
        } else {
            return seatTypeOther;
        }
    }

    private void changeSeatType(String seatType, CheckOrderDTO checkOrderDTO, QueueCountDTO queueCountDTO, DoOrderDTO doOrderDTO) {
        String newPassengerTicketStr = getNewPassengerTicketStr(seatType, checkOrderDTO.getPassengerTicketStr());
        checkOrderDTO.setPassengerTicketStr(newPassengerTicketStr);
        doOrderDTO.setPassengerTicketStr(newPassengerTicketStr);
        queueCountDTO.setSeatType(seatType);
        logger.info("已改变席别，编号：{}", seatType);
    }

    private String getNewPassengerTicketStr(String seatType, String oldPassengerTicketStr) {
        String[] passengers = oldPassengerTicketStr.split("_");
        String newPassengerTicketStr = "";
        for (String str : passengers) {
            String[] strs = str.split(",");
            newPassengerTicketStr += "_" + str.replaceFirst(strs[0], seatType);
        }
        return newPassengerTicketStr.replaceFirst("_", "");
    }

    private boolean checkSeatAvailable(String queueCountData) {
        String result = passengerService.getQueueCount(queueCountData);
        if (result.startsWith("{")) {
            JSONObject resultJson = JSON.parseObject(result);
            if ("[]".equals(resultJson.getString("messages"))) {
                String ticket = resultJson.getJSONObject("data").getString("ticket");
                if (!"0".equals(ticket.split(",")[0])) {
                    return true;
                }
            }
        }
        logger.info("余票不足（不包括无座）");
        return false;
    }
    /*---------------------------------------------------END: 抢票选项--------------------------------------------------------*/

    /*---------------------------------------------------START: 组装数据------------------------------------------------------*/
    private String buildQueryData(RobParamsDTO robParamsDTO) {
        QueryDTO queryDTO = new QueryDTO();
        queryDTO.setTrainDate(robParamsDTO.getTrainDate());
        queryDTO.setFromStation(robParamsDTO.getFromStation());
        queryDTO.setToStation(robParamsDTO.getToStation());
        queryDTO.setPurposeCodes(robParamsDTO.getPurposeCodes());
        // 实体类注解保证字段顺序
        return JSON.toJSONString(queryDTO);
    }

    private String buildOrderRequestData(RobParamsDTO robParamsDTO, String scretStr) {
        OrderSubmitDTO orderSubmitDTO = new OrderSubmitDTO();
        try {
            orderSubmitDTO.setSecretStr(URLDecoder.decode(scretStr, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        orderSubmitDTO.setTrainDate(robParamsDTO.getTrainDate());
        orderSubmitDTO.setBackTrainDate("");
        orderSubmitDTO.setTourFlag(robParamsDTO.getTourFlag());
        orderSubmitDTO.setPurposeCodes(robParamsDTO.getPurposeCodes());
        orderSubmitDTO.setQueryFromStationName(robParamsDTO.getFromStationName());
        orderSubmitDTO.setQueryToStationName(robParamsDTO.getToStationName());
        return JSON.toJSONString(orderSubmitDTO);
    }

    private Map<String, String> buildOrderParamsMap(String result) {
        Map<String, String> map = new HashMap<>();
        boolean isGet = putToOrderParamsMap("ticketInfoForPassengerForm", "=", 0, ";", result, map);
        if (!isGet) {
            return null;
        }
        putToOrderParamsMap("orderRequestDTO", "=", 0, ";", result, map);
        putToOrderParamsMap("globalRepeatSubmitToken", " = '", 0, "'", result, map);
        return map;
    }

    private Boolean putToOrderParamsMap(String name, String afterName, int interval, String endStr, String result, Map<String, String> map) {
        String value = StrUtils.findVlaue(name, afterName, interval, endStr, result);
        logger.info("已获得订单参数{}:{}", name, value);
        map.put(name, value);
        return value != null ? true : false;
    }

    private String buildCheckOrderData(CheckOrderDTO checkOrderDTO, TicketInfoDTO ticketInfoDTO, String globalRepeatSubmitToken) {
        checkOrderDTO.setTourFlag(ticketInfoDTO.getTourFlag());
        checkOrderDTO.setGlobalRepeatSubmitToken(globalRepeatSubmitToken);
        return JSON.toJSONString(checkOrderDTO);
    }

    private String buildQueueCountData(QueueCountDTO queueCountDTO, OrderRequestDTO orderRequestDTO,
                                       TicketInfoDTO ticketInfoDTO, String globalRepeatSubmitToken) {
        Date trainDate = new Date(orderRequestDTO.getTrainDate().getTime());
        queueCountDTO.setTrainDate(trainDate.toString());
        queueCountDTO.setTrainNo(orderRequestDTO.getTrainNo());
        queueCountDTO.setStationTrainCode(orderRequestDTO.getStationTrainCode());
        queueCountDTO.setFromStationTelecode(orderRequestDTO.getFromStationTelecode());
        queueCountDTO.setToStationTelecode(orderRequestDTO.getToStationTelecode());

        queueCountDTO.setLeftTicket(ticketInfoDTO.getLeftTicketStr());
        queueCountDTO.setPurposeCodes(ticketInfoDTO.getPurposeCodes());
        queueCountDTO.setTrainLocation(ticketInfoDTO.getTrainLocation());

        queueCountDTO.setGlobalRepeatSubmitToken(globalRepeatSubmitToken);
        return JSON.toJSONString(queueCountDTO);
    }

    private String buildDoOrderData(DoOrderDTO doOrderDTO, TicketInfoDTO ticketInfoDTO) {
        doOrderDTO.setPurposeCodes(ticketInfoDTO.getPurposeCodes());
        doOrderDTO.setKeyCheckIsChange(ticketInfoDTO.getKeyCheckIsChange());
        doOrderDTO.setLeftTicketStr(ticketInfoDTO.getLeftTicketStr());
        doOrderDTO.setTrainLocation(ticketInfoDTO.getTrainLocation());
        return JSON.toJSONString(doOrderDTO);
    }
    /*---------------------------------------------------END: 组装数据--------------------------------------------------------*/
}
