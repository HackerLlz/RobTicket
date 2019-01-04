package com.example.frp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.frp.common.tool.HttpUtils;
import com.example.frp.common.tool.MailSendUtils;
import com.example.frp.common.tool.StrUtils;
import com.example.frp.entity.DTO.RobRequestDTO;
import com.example.frp.service.PassengerService;
import com.example.frp.service.RobService;
import com.example.frp.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
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
    private static final String BASE_URL = "https://kyfw.12306.cn/otn/";

    @Autowired
    private TicketService ticketService;

    @Autowired
    PassengerService passengerService;

    public Boolean doRob(String payload) throws Exception{
        logger.info("尝试抢票，入参：{}", payload);
        JSONObject jsonObject = JSON.parseObject(payload);
        RobRequestDTO robRequestDTO = JSON.parseObject(JSON.toJSONString(jsonObject.get("robRequestData")), RobRequestDTO.class);

        String queryResult = ticketService.listTicket(buildQueryData(robRequestDTO));
        String secretStr = findSecretStr(robRequestDTO.getTrainNumber(), queryResult);
        if (!StringUtils.isEmpty(secretStr)) {
            robRequestDTO.setSecretStr(secretStr);
            boolean isSubmit = submitOrderRequest(buildOrderRequestData(robRequestDTO));
            if (isSubmit) {
                Map<String, String> orderParamsMap = getOrderParamsMap();
                if (orderParamsMap == null) {
                    logger.info("抢票失败：订单参数获取失败");
                    return false;
                }
                boolean isCheck = checkOrder(buildCheckOrderData(jsonObject, orderParamsMap));
                if (isCheck) {
                    boolean robNoSeat = (Boolean)jsonObject.get("robNoSeat");
                    if (!robNoSeat) {
                        boolean isSeatAvailable =  checkSeatAvailable(buildQueueCountData(jsonObject, orderParamsMap));
                        if (!isSeatAvailable) {
                            logger.info("抢票失败：余票不足（不包括无座）");
                            return false;
                        }
                    }
                    boolean isOrder = doOrder(buildDoOrderData(jsonObject, orderParamsMap));
                    if (isOrder) {
                        logger.info("抢票成功，发送邮件");
                        sendMail();
                        return true;
                    }
                }
            }
        }
        logger.info("抢票失败");
        return false;
    }

    /*---------------------------------------------------START: 抢票流程------------------------------------------------------*/
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
        String result = ticketService.submitOrderRequest(data);
        if (result.startsWith("{") && "[]".equals(JSON.parseObject(result).get("messages").toString())) {
            return true;
        }
        return false;
    }

    private Map<String, String> getOrderParamsMap() {
        logger.info("获取订单参数");
        String url = BASE_URL + "confirmPassenger/initDc";
        String result = HttpUtils.doPostForm(url, null, true);
        return buildOrderParamsMap(result);
    }

    private boolean checkOrder(String checkOrderData) {
        String result = passengerService.checkOrderInfo(checkOrderData);
        if (result.startsWith("{") && "[]".equals(JSON.parseObject(result).get("messages").toString())) {
            return true;
        }
        return false;
    }

    private boolean checkSeatAvailable(String queueCountData) {
        String result = passengerService.getQueueCount(queueCountData);
        if (result.startsWith("{") && "[]".equals(JSON.parseObject(result).get("messages").toString())) {
            String ticket = (String)JSON.parseObject(JSON.toJSONString(JSON.parseObject(result).get("data"))).get("ticket");
            if (!"0".equals(ticket.split(",")[0])) {
                return true;
            }
        }
        return false;
    }

    private boolean doOrder(String data) {
        String url = "confirmPassenger/confirmSingleForQueue";
        String result = passengerService.doOrder(url, data);
        if (result.startsWith("{") && "{\"submitStatus\":true}".equals(JSON.parseObject(result).get("data").toString())) {
            return true;
        }
        return false;
    }

    private void sendMail() throws Exception{
        MailSendUtils.sendHtmlMessage("1174827250@qq.com", "抢票成功", "恭喜您！抢票成功");
    }
    /*---------------------------------------------------END: 抢票流程--------------------------------------------------------*/

    /*---------------------------------------------------START: 建立数据------------------------------------------------------*/
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

    private String buildCheckOrderData(JSONObject payloadJson, Map<String, String> orderParamsMap) {
        JSONObject checkOrderJson = JSON.parseObject(JSON.toJSONString(payloadJson.get("checkOrderData")));
        JSONObject ticketFormJson = JSON.parseObject(orderParamsMap.get("ticketInfoForPassengerForm"));
        JSONObject jsonObject = new JSONObject();
        putToJsonObject(jsonObject, checkOrderJson, "cancel_flag");
        putToJsonObject(jsonObject, checkOrderJson, "bed_level_order_num");
//        putToJsonObject(jsonObject, checkOrderJson, "passengerTicketStr");
//        putToJsonObject(jsonObject, checkOrderJson, "oldPassengerStr");
        putToJsonObject(jsonObject, checkOrderJson, "randCode");
        putToJsonObject(jsonObject, checkOrderJson, "whatsSelect");
        putToJsonObject(jsonObject, checkOrderJson, "_json_att");
        putToJsonObject(jsonObject, checkOrderJson, "REPEAT_SUBMIT_TOKEN");

        putToJsonObject(jsonObject, ticketFormJson, "tour_flag");

        putToJsonObject(jsonObject, payloadJson, "passengerTicketStr");
        putToJsonObject(jsonObject, payloadJson, "oldPassengerStr");
        return JSON.toJSONString(jsonObject);
    }

    private String buildQueueCountData(JSONObject payloadJson, Map<String, String> orderParamsMap) {
        JSONObject ticketFormJson = JSON.parseObject(orderParamsMap.get("ticketInfoForPassengerForm"));
        JSONObject orderRequestJson = JSON.parseObject(orderParamsMap.get("orderRequestDTO"));
        JSONObject queueCountJson = JSON.parseObject(JSON.toJSONString(payloadJson.get("queueCountData")));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("seatType", ((String) payloadJson.get("passengerTicketStr")).substring(0, 1));
//        putToJsonObject(jsonObject, queueCountJson, "seatType");
//        putToJsonObject(jsonObject, queueCountJson, "_json_att");
//        putToJsonObject(jsonObject, queueCountJson, "isCheckOrderInfo");
//        putToJsonObject(jsonObject, queueCountJson, "REPEAT_SUBMIT_TOKEN");

        putToJsonObject(jsonObject, orderRequestJson, "train_no");
        jsonObject.put("stationTrainCode", orderRequestJson.get("station_train_code"));
        jsonObject.put("fromStationTelecode", orderRequestJson.get("from_station_telecode"));
        jsonObject.put("toStationTelecode", orderRequestJson.get("to_station_telecode"));
        Date date = new Date((Long) JSON.parseObject(JSON.toJSONString(orderRequestJson.get("train_date"))).get("time"));
        jsonObject.put("train_date", date.toString());

        putToJsonObject(jsonObject, ticketFormJson, "purpose_codes");
        putToJsonObject(jsonObject, ticketFormJson, "train_location");
        // secretStr要解码，leftTicketStr就不用，很奇怪
        jsonObject.put("leftTicket", ticketFormJson.get("leftTicketStr"));
        return JSON.toJSONString(jsonObject);
    }

    private String buildDoOrderData(JSONObject payloadJson, Map<String, String> orderParamsMap) {
        JSONObject ticketFormJson = JSON.parseObject(orderParamsMap.get("ticketInfoForPassengerForm"));
        JSONObject jsonObject = new JSONObject();
        putToJsonObject(jsonObject, payloadJson, "passengerTicketStr");
        putToJsonObject(jsonObject, payloadJson, "oldPassengerStr");
        putToJsonObject(jsonObject, payloadJson, "randCode");
        putToJsonObject(jsonObject, payloadJson, "choose_seats");
        putToJsonObject(jsonObject, payloadJson, "seatDetailType");
        putToJsonObject(jsonObject, payloadJson, "whatsSelect");
        putToJsonObject(jsonObject, payloadJson, "roomType");
        putToJsonObject(jsonObject, payloadJson, "dwAll");
        putToJsonObject(jsonObject, ticketFormJson, "purpose_codes");
        putToJsonObject(jsonObject, ticketFormJson, "key_check_isChange");
        putToJsonObject(jsonObject, ticketFormJson, "leftTicketStr");
        putToJsonObject(jsonObject, ticketFormJson, "train_location");
        return JSON.toJSONString(jsonObject);
    }

    private void putToJsonObject(JSONObject jsonObject, JSONObject json, String name) {
        String value = (String)json.get(name);
        jsonObject.put(name, value);
    }

    private Boolean putToOrderParamsMap(String name, String afterName, int interval, String endStr, String result, Map<String, String> map) {
        String value = StrUtils.findVlaue(name, afterName, interval, endStr, result);
        logger.info("已获得订单参数{}:{}", name, value);
        map.put(name, value);
        return (value.startsWith("{") || value.startsWith("["))? true: false;
    }
    /*---------------------------------------------------END: 建立数据--------------------------------------------------------*/
}
