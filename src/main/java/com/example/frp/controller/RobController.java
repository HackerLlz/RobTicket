package com.example.frp.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.frp.common.constant.AjaxMessage;
import com.example.frp.common.tool.HttpUtils;
import com.example.frp.common.tool.StrUtils;
import com.example.frp.entity.DTO.RobRequestDTO;
import com.example.frp.service.TicketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-29 13:26
 */
@Controller
@RequestMapping("/rob")
public class RobController {
    private static final Logger logger = LoggerFactory.getLogger(TicketController.class);
    private static final String BASE_URL = "https://kyfw.12306.cn/otn/";

    @Autowired
    private TicketService ticketService;

    @RequestMapping(value = "view", method = RequestMethod.POST)
    public String view(RobRequestDTO robRequestDTO, Model model) {
        logger.info("开始跳转到抢票界面，入参：{}", robRequestDTO.toString());
        model.addAttribute("robRequestData", robRequestDTO);
        return "rob/view";
    }

    @RequestMapping(value = "doRob", method = RequestMethod.POST)
    @ResponseBody
    public String doRob(@RequestBody String payload) {
        logger.info("开始抢票，入参：{}", payload);
        JSONObject jsonObject = JSON.parseObject(payload);
        RobRequestDTO robRequestDTO = JSON.parseObject(JSON.toJSONString(jsonObject.get("robRequestData")), RobRequestDTO.class);
        String checkOrderData = JSON.toJSONString(jsonObject.get("checkOrderData"));
        // 循环
        String secretStr = null;
        while (StringUtils.isEmpty(secretStr)){
            String queryResult = ticketService.listTicket(buildQueryData(robRequestDTO));
            secretStr = findSecretStr(robRequestDTO.getTrainNumber(), queryResult);
        }
        robRequestDTO.setSecretStr(secretStr);
        boolean isSubmit = submitOrderRequest(buildOrderRequestData(robRequestDTO));
        if (isSubmit) {
            String ticketForm = getTicketInfoForPassengerForm();
            boolean isCheck = checkOrder(buildCheckOrderData(checkOrderData, ticketForm));
            if (isCheck) {
                boolean isOrder = doOrder(buildDoOrderData(payload, ticketForm));
                if (isOrder) {
                    logger.info("抢票成功！！！");
                    return AjaxMessage.SUCCESS;
                }
            }
        }
        logger.info("抢票失败！！！");
        return AjaxMessage.FAIL;
    }

    private String buildQueryData(RobRequestDTO robRequestDTO) {
        JSONObject linkJson = new JSONObject(new LinkedHashMap<>());
        linkJson.put("leftTicketDTO.train_date", robRequestDTO.getTrainDate());
        linkJson.put("leftTicketDTO.from_station", robRequestDTO.getFromStationCode());
        linkJson.put("leftTicketDTO.to_station", robRequestDTO.getToStationCode());
        linkJson.put("purpose_codes", robRequestDTO.getPurposeCodes());
        return JSON.toJSONString(linkJson);
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
        logger.info("提交订单请求， 入参:{}", data);
        String url = BASE_URL + "leftTicket/submitOrderRequest";
        String result = HttpUtils.doPostForm(url, data, true);
        if (result.startsWith("{") && "[]".equals(JSON.parseObject(result).get("messages").toString())) {
            return true;
        }
        return false;
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

    private String getTicketInfoForPassengerForm() {
        logger.info("获取订单参数");
        String url = BASE_URL + "confirmPassenger/initDc";
        String result = HttpUtils.doPostForm(url, null, true);
        return StrUtils.findVlaue("ticketInfoForPassengerForm", "=", 0, ";", result);
    }

    private String buildCheckOrderData(String checkOrderData, String ticketForm) {
        JSONObject checkOrderJson = JSON.parseObject(checkOrderData);
        JSONObject ticketFormJson = JSON.parseObject(ticketForm);
        JSONObject jsonObject = new JSONObject();
        putToJsonObject(jsonObject, checkOrderJson, "cancel_flag");
        putToJsonObject(jsonObject, checkOrderJson, "bed_level_order_num");
        putToJsonObject(jsonObject, checkOrderJson, "passengerTicketStr");
        putToJsonObject(jsonObject, checkOrderJson, "oldPassengerStr");
        putToJsonObject(jsonObject, checkOrderJson, "randCode");
        putToJsonObject(jsonObject, checkOrderJson, "whatsSelect");
        putToJsonObject(jsonObject, checkOrderJson, "_json_att");
        putToJsonObject(jsonObject, checkOrderJson, "REPEAT_SUBMIT_TOKEN");
        putToJsonObject(jsonObject, ticketFormJson, "tour_flag");
        return JSON.toJSONString(jsonObject);
    }

    private boolean checkOrder(String checkOrderData) {
        logger.info("验证订单， 入参:{}", checkOrderData);
        String url = BASE_URL + "confirmPassenger/checkOrderInfo";
        String result = HttpUtils.doPostForm(url, checkOrderData, true);
        if (result.startsWith("{") && "[]".equals(JSON.parseObject(result).get("messages").toString())) {
            return true;
        }
        return false;
    }

    private String buildDoOrderData(String payload, String ticketForm) {
        JSONObject payloadJson = JSON.parseObject(payload);
        JSONObject ticketFormJson = JSON.parseObject(ticketForm);
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
        // secretStr要解码，这个就不用，很奇怪
//        if ("leftTicketStr".equals(name)){
//            try {
//                value = URLDecoder.decode(value, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
        jsonObject.put(name, value);
    }

    private boolean doOrder(String data) {
        logger.info("提交订单， 入参:{}", data);
        String url = BASE_URL + "confirmPassenger/confirmSingleForQueue";
        String result = HttpUtils.doPostForm(url, data, true);
        if (result.startsWith("{") && "{\"submitStatus\":true}".equals(JSON.parseObject(result).get("data").toString())) {
            return true;
        }
        return false;
    }
}
