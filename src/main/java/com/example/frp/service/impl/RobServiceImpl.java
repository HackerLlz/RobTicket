package com.example.frp.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.frp.common.constant.UrlConstant;
import com.example.frp.common.tool.HttpUtils;
import com.example.frp.common.tool.StrUtils;
import com.example.frp.entity.DTO.RobRequestDTO;
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

    @Autowired
    private TicketService ticketService;

    @Autowired
    PassengerService passengerService;

    @Autowired
    LoginService loginService;

    public Boolean doRob(String payload){
        logger.info("尝试抢票，入参：{}", payload);
        boolean isLogin = keepLogin();
        if (isLogin) {
            JSONObject jsonObject = JSON.parseObject(payload);
            RobRequestDTO robRequestDTO = JSON.parseObject(jsonObject.getString("robRequestData"), RobRequestDTO.class);

            String queryResult = ticketService.listTicket(buildQueryData(robRequestDTO));
            String secretStr = findSecretStr(robRequestDTO.getTrainNumber(), queryResult);
            if (!StringUtils.isEmpty(secretStr)) {
                robRequestDTO.setSecretStr(secretStr);
                boolean isSubmit = submitOrderRequest(buildOrderRequestData(robRequestDTO));
                if (isSubmit) {
                    Map<String, String> orderParamsMap = getOrderParamsMap();
                    if (orderParamsMap != null) {
                        boolean isCheck = checkOrder(buildCheckOrderData(jsonObject, orderParamsMap));
                        if (isCheck) {
                            boolean robNoSeat = jsonObject.getBoolean("robNoSeat");
                            if (!robNoSeat) {
                                boolean isSeatAvailable =  checkSeatAvailable(buildQueueCountData(jsonObject, orderParamsMap));
                                if (!isSeatAvailable) {
                                    logger.info("抢票失败");
                                    return false;
                                }
                            }
//                            boolean isOrder = doOrder(buildDoOrderData(jsonObject, orderParamsMap));
//                            if (isOrder) {
//                                logger.info("抢票成功");
//                                return true;
//                            }
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

    private boolean checkOrder(String checkOrderData) {
        String result = passengerService.checkOrderInfo(checkOrderData);
        if (result.startsWith("{") && "[]".equals(JSON.parseObject(result).getString("messages"))) {
            return true;
        }
        return false;
    }

    private boolean checkSeatAvailable(String queueCountData) {
        String result = passengerService.getQueueCount(queueCountData);
        JSONObject resultJson = JSON.parseObject(result);
        if (result.startsWith("{") && "[]".equals(resultJson.getString("messages"))) {
            String ticket = resultJson.getJSONObject("data").getString("ticket");
            if (!"0".equals(ticket.split(",")[0])) {
                return true;
            }
        }
        logger.info("余票不足（不包括无座）");
        return false;
    }

    private boolean doOrder(String data) {
        String url = "confirmPassenger/confirmSingleForQueue";
        String result = passengerService.doOrder(url, data);
        if (result.startsWith("{") && "{\"submitStatus\":true}".equals(JSON.parseObject(result).getString("data"))) {
            return true;
        }
        logger.info("确认下单失败");
        return false;
    }
    /*---------------------------------------------------END: 抢票流程--------------------------------------------------------*/

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

    private String buildCheckOrderData(JSONObject payloadJson, Map<String, String> orderParamsMap) {
        JSONObject checkOrderJson = payloadJson.getJSONObject("checkOrderData");
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
        JSONObject queueCountJson = payloadJson.getJSONObject("queueCountData");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("seatType", payloadJson.getString("passengerTicketStr").substring(0, 1));
//        putToJsonObject(jsonObject, queueCountJson, "seatType");
//        putToJsonObject(jsonObject, queueCountJson, "_json_att");
//        putToJsonObject(jsonObject, queueCountJson, "isCheckOrderInfo");
//        putToJsonObject(jsonObject, queueCountJson, "REPEAT_SUBMIT_TOKEN");

        putToJsonObject(jsonObject, orderRequestJson, "train_no");
        jsonObject.put("stationTrainCode", orderRequestJson.get("station_train_code"));
        jsonObject.put("fromStationTelecode", orderRequestJson.get("from_station_telecode"));
        jsonObject.put("toStationTelecode", orderRequestJson.get("to_station_telecode"));
        Date date = new Date(orderRequestJson.getJSONObject("train_date").getLong("time"));
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
        String value = json.getString(name);
        jsonObject.put(name, value);
    }

    private Boolean putToOrderParamsMap(String name, String afterName, int interval, String endStr, String result, Map<String, String> map) {
        String value = StrUtils.findVlaue(name, afterName, interval, endStr, result);
        logger.info("已获得订单参数{}:{}", name, value);
        map.put(name, value);
        return value != null?true: false;
    }
    /*---------------------------------------------------END: 组装数据--------------------------------------------------------*/
}
