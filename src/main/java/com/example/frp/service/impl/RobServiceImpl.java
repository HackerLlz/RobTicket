package com.example.frp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.frp.common.constant.UrlConstant;
import com.example.frp.common.tool.HttpUtils;
import com.example.frp.common.tool.StrUtils;
import com.example.frp.entity.DTO.RobRequestDTO;
import com.example.frp.entity.DTO.orderSubmitParams.OrderRequestDTO;
import com.example.frp.entity.DTO.orderSubmitParams.TicketInfoDTO;
import com.example.frp.entity.DTO.robProcess.*;
import com.example.frp.service.LoginService;
import com.example.frp.service.PassengerService;
import com.example.frp.service.RobService;
import com.example.frp.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    private static final List<String> seatTypeGD = Arrays.asList("O", "M", "9");
    private static final List<String> seatTypeOther = Arrays.asList("1", "3", "4");

    @Autowired
    private TicketService ticketService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private LoginService loginService;

    public Boolean doRob(String payload){
        logger.info("尝试抢票，入参：{}", payload);
        boolean isLogin = keepLogin();
        if (isLogin) {
            JSONObject jsonObject = JSON.parseObject(payload);
            RobRequestDTO robRequestDTO = JSON.parseObject(jsonObject.getString("robRequestData"), RobRequestDTO.class);
            CheckOrderDTO checkOrderDTO = JSON.parseObject(jsonObject.getString("checkOrderData"), CheckOrderDTO.class);
            QueueCountDTO queueCountDTO = JSON.parseObject(jsonObject.getString("queueCountData"), QueueCountDTO.class);
            DoOrderDTO doOrderDTO = JSON.parseObject(jsonObject.getString("doOrderData"), DoOrderDTO.class);
            RobParamsDTO robParamsDTO = JSON.parseObject(jsonObject.getString("robParamsData"), RobParamsDTO.class);

            String queryResult = ticketService.listTicket(buildQueryData(robRequestDTO));
            String secretStr = findSecretStr(robRequestDTO.getTrainNumber(), queryResult);
            if (!StringUtils.isEmpty(secretStr)) {
                robRequestDTO.setSecretStr(secretStr);
                boolean isSubmit = submitOrderRequest(buildOrderRequestData(robRequestDTO));
                if (isSubmit) {
                    Map<String, String> orderParamsMap = getOrderParamsMap();
                    if (orderParamsMap != null) {
                        OrderRequestDTO orderRequestDTO = JSON.parseObject(orderParamsMap.get("orderRequestDTO"), OrderRequestDTO.class);
                        TicketInfoDTO ticketInfoDTO = JSON.parseObject(orderParamsMap.get("ticketInfoForPassengerForm"), TicketInfoDTO.class);
                        boolean isAllSeatType = checkAllSeatType(checkOrderDTO);
                        if (isAllSeatType) {
                            List<String> seatTypeList = chooseSeatTypeList(robRequestDTO);
                            for (String seatType: seatTypeList) {
                                changeSeatType(seatType, checkOrderDTO, queueCountDTO, doOrderDTO);
                                boolean isSuccess = submitOrder(checkOrderDTO, queueCountDTO, robParamsDTO,
                                        doOrderDTO, ticketInfoDTO, orderRequestDTO);
                                if (isSuccess) {
                                    return true;
                                }
                            }
                        } else {
                            boolean isSuccess = submitOrder(checkOrderDTO, queueCountDTO, robParamsDTO,
                                    doOrderDTO, ticketInfoDTO, orderRequestDTO);
                            if (isSuccess) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        logger.info("抢票失败");
        return false;
    }

    /*---------------------------------------------------START: 抢票流程------------------------------------------------------*/
    private Boolean keepLogin() {
        String payload = "{\"appid\": \"otn\"}";
        String result = loginService.uamtk(payload);
        if (result.startsWith("{")) {
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject.getInteger("result_code") == 0) {
                String tk = jsonObject.getString("newapptk");
                payload = "{\"tk\":\"" + tk + "\"}";
                result = loginService.uamtkClient(payload);
                if (result.startsWith("{")) {
                    return true;
                }
            }
        }
        logger.info("保持登陆失败");
        return false;
    }

    private String findSecretStr(String trainNumber, String result) {
        String secretStr = null;
        secretStr = StrUtils.reverseFindVlaue(trainNumber, "|预订|", 0, "\"", result);
        if (!StringUtils.isEmpty(secretStr)){
            logger.info("已获得secretStr:{}", secretStr);
            return secretStr;
        }
        logger.info("未获得secretStr");
        return secretStr;
    }

    private boolean submitOrderRequest(String data){
        logger.info("抢票车次：{}");
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
                               TicketInfoDTO ticketInfoDTO, OrderRequestDTO orderRequestDTO) {
        boolean isCheck = checkOrder(buildCheckOrderData(checkOrderDTO, ticketInfoDTO));
        if (isCheck) {
            boolean robNoSeat = robParamsDTO.getRobNoSeat();
            if (!robNoSeat) {
                boolean isSeatAvailable =  checkSeatAvailable(buildQueueCountData(queueCountDTO, orderRequestDTO, ticketInfoDTO));
                if (!isSeatAvailable) {
                    return false;
                }
            }
            boolean isOrder = doOrder(buildDoOrderData(doOrderDTO, ticketInfoDTO));
            if (isOrder) {
                logger.info("抢票成功");
                return true;
            }
        }
        return false;
    }

    private boolean checkOrder(String checkOrderData) {
        String result = passengerService.checkOrderInfo(checkOrderData);
        if (result.startsWith("{") && "[]".equals(JSON.parseObject(result).getString("messages"))) {
            return true;
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
    private Boolean checkAllSeatType(CheckOrderDTO checkOrderDTO) {
        String[] passengers = checkOrderDTO.getPassengerTicketStr().split("_");
        for (String str: passengers) {
            if (str.startsWith("all")) {
                return true;
            }
        }
        return false;
    }

    private List<String> chooseSeatTypeList(RobRequestDTO robRequestDTO) {
        String trainNumber = robRequestDTO.getTrainNumber();
        if (trainNumber.contains("G") || trainNumber.contains("D")) {
            return seatTypeGD;
        } else {
            return seatTypeOther;
        }
    }

    private void changeSeatType(String seatType, CheckOrderDTO checkOrderDTO, QueueCountDTO queueCountDTO, DoOrderDTO doOrderDTO) {
        checkOrderDTO.setPassengerTicketStr(getNewPassengerTicketStr(seatType, checkOrderDTO.getPassengerTicketStr()));
        doOrderDTO.setPassengerTicketStr(getNewPassengerTicketStr(seatType, doOrderDTO.getPassengerTicketStr()));
        queueCountDTO.setSeatType(seatType);
        logger.info("已改变席别，编号：{}", seatType);
    }

    private String getNewPassengerTicketStr(String seatType,String oldPassengerTicketStr) {
        String[] passengers = oldPassengerTicketStr.split("_");
        String newPassengerTicketStr = "";
        for (String str: passengers) {
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
    private String buildQueryData(RobRequestDTO robRequestDTO) {
        JSONObject linkJson = new JSONObject(new LinkedHashMap<>());
        linkJson.put("leftTicketDTO.train_date", robRequestDTO.getTrainDate());
        linkJson.put("leftTicketDTO.from_station", robRequestDTO.getFromStationCode());
        linkJson.put("leftTicketDTO.to_station", robRequestDTO.getToStationCode());
        linkJson.put("purpose_codes", robRequestDTO.getPurposeCodes());
        return JSON.toJSONString(linkJson);
    }

    private String buildOrderRequestData(RobRequestDTO robRequestDTO) {
        JSONObject linkJson = new JSONObject(new LinkedHashMap<>());
        try {
            linkJson.put("secretStr", URLDecoder.decode(robRequestDTO.getSecretStr(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        linkJson.put("train_date", robRequestDTO.getTrainDate());
        linkJson.put("back_train_date", robRequestDTO.getBackTrainDate());
        linkJson.put("tour_flag", robRequestDTO.getTourFlag());
        linkJson.put("purpose_codes", robRequestDTO.getPurposeCodes());
        linkJson.put("query_from_station_name", robRequestDTO.getFromStation());
        linkJson.put("query_to_station_name", robRequestDTO.getToStation());
        return JSON.toJSONString(linkJson);
    }

    private Map<String, String> buildOrderParamsMap(String result) {
        Map<String, String> map = new HashMap<>();
        boolean isGet = putToOrderParamsMap("ticketInfoForPassengerForm", "=", 0, ";", result, map);
        if (!isGet) {
            return null;
        }
        putToOrderParamsMap("orderRequestDTO", "=", 0, ";", result, map);
        return map;
    }

    private Boolean putToOrderParamsMap(String name, String afterName, int interval, String endStr, String result, Map<String, String> map) {
        String value = StrUtils.findVlaue(name, afterName, interval, endStr, result);
        logger.info("已获得订单参数{}:{}", name, value);
        map.put(name, value);
        return value != null? true: false;
    }

    private String buildCheckOrderData(CheckOrderDTO checkOrderDTO, TicketInfoDTO ticketInfoDTO) {
        checkOrderDTO.setTourFlag(ticketInfoDTO.getTourFlag());
        return JSON.toJSONString(checkOrderDTO);
    }

    private String buildQueueCountData(QueueCountDTO queueCountDTO, OrderRequestDTO orderRequestDTO, TicketInfoDTO ticketInfoDTO) {
        Date trainDate = new Date(orderRequestDTO.getTrainDate().getTime());
        queueCountDTO.setTrainDate(trainDate.toString());
        queueCountDTO.setTrainNo(orderRequestDTO.getTrainNo());
        queueCountDTO.setStationTrainCode(orderRequestDTO.getStationTrainCode());
        queueCountDTO.setFromStationTelecode(orderRequestDTO.getFromStationTelecode());
        queueCountDTO.setToStationTelecode(orderRequestDTO.getToStationTelecode());

        queueCountDTO.setLeftTicket(ticketInfoDTO.getLeftTicket());
        queueCountDTO.setPurposeCodes(ticketInfoDTO.getPurposeCodes());
        queueCountDTO.setTrainLocation(ticketInfoDTO.getTrainLocation());
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
