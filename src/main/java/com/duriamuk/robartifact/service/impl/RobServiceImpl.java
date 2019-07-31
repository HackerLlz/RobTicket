package com.duriamuk.robartifact.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.constant.PrefixName;
import com.duriamuk.robartifact.common.constant.UrlConstant;
import com.duriamuk.robartifact.common.constant.ValueConstant;
import com.duriamuk.robartifact.common.exception.RobException;
import com.duriamuk.robartifact.common.schedule.RobScheduledThreadPool;
import com.duriamuk.robartifact.common.schedule.RobTask;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.common.tool.RedisUtils;
import com.duriamuk.robartifact.common.tool.StrUtils;
import com.duriamuk.robartifact.entity.DTO.orderSubmitParams.OrderRequestDTO;
import com.duriamuk.robartifact.entity.DTO.orderSubmitParams.QueryLeftNewDetailDTO;
import com.duriamuk.robartifact.entity.DTO.orderSubmitParams.TicketInfoDTO;
import com.duriamuk.robartifact.entity.DTO.robProcess.*;
import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;
import com.duriamuk.robartifact.mapper.RobMapper;
import com.duriamuk.robartifact.mapper.UserMapper;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yy-MM-dd");

    @Autowired
    private TicketService ticketService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private RobMapper robMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Boolean insertRobRecord(RobParamsDTO robParamsDTO, RobParamsOtherDTO robParamsOtherDTO) {
        logger.info("插入抢票任务信息");
        List<RobParamsDTO> robParamsDTOList = listRobRecordByUserId(robParamsDTO.getUserId());
        int goingRobTaskCount = countGoingRobTask(robParamsDTOList);
        if (goingRobTaskCount <= MAX_ROB_TASK) {
            robParamsDTO.setStatus(1);
            Long id = robParamsDTO.getId();
            robMapper.insertRobRecord(robParamsDTO);
            if (!ObjectUtils.isEmpty(id)) {
                // 由于id冲突，变为update语句，获得最后插入的id时会获得之前other表插入的id值
                robParamsDTO.setId(id);
            }
            robParamsOtherDTO.setRobId(robParamsDTO.getId());
            robMapper.insertRobRecordOther(robParamsOtherDTO);
            logger.info("插入抢票任务成功");
            return true;
        }
        logger.info("插入抢票任务失败");
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
    public List<RobParamsDTO> listRobRecordWithOther(RobParamsDTO robParamsDTO) {
        return robMapper.listRobRecordWithOther(robParamsDTO);
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

    @Override
    public void restartTask(RobParamsDTO robParamsDTO) {
        logger.info("重启抢票任务，入参：{}", robParamsDTO.toString());
        List<RobParamsDTO> robParamsDTOList = listRobRecordWithOther(robParamsDTO);
        for (RobParamsDTO rob : robParamsDTOList) {
            long id = rob.getId();
            long userId = rob.getUserId();
            RedisUtils.setWithExpire(PrefixName.TABLE_ROB_RECORD + id, true, ValueConstant.ROB_TASK_EXPIRE_TIME, TimeUnit.DAYS);
            RobScheduledThreadPool.schedule(new RobTask(buildPayload(rob), 1, id, userId));
            logger.info("重启抢票任务userId:{};id:{}", userId, id);
        }
    }

    @Override
    public void stopTaskByUserId(Long userId){
        logger.info("停止抢票任务，入参：{}", userId);
        List<RobParamsDTO> robParamsDTOList = listRobRecordByUserId(userId);
        for (RobParamsDTO robParamsDTO : robParamsDTOList) {
            RedisUtils.setWithExpire(PrefixName.TABLE_ROB_RECORD + robParamsDTO.getId(), null, 0);
        }
    }

    private String buildPayload(RobParamsDTO robParamsDTO) {
        JSONObject jsonObject = new JSONObject();
        RobParamsOtherDTO robParamsOtherDTO = robParamsDTO.getRobParamsOtherDTO();
        jsonObject.put("userInfoPO", buildUserInfoPO(robParamsDTO));
        jsonObject.put("checkOrderData", buildCheckOrderDTO(robParamsOtherDTO));
        jsonObject.put("queueCountData", buildQueueCountDTO(robParamsOtherDTO));
        jsonObject.put("doOrderData", buildDoOrderDTO(robParamsOtherDTO));
        jsonObject.put("robParamsData", robParamsDTO);
        return jsonObject.toJSONString();
    }

    private UserInfoPO buildUserInfoPO(RobParamsDTO robParamsDTO) {
        // 密码因为比较重要，每次在service里用username去获取
        UserInfoPO userInfoPO = new UserInfoPO();
        userInfoPO.setId(robParamsDTO.getUserId());
        UserInfoPO userInfo = userMapper.getUserInfo(userInfoPO);
        userInfoPO.setUsername(userInfo.getUsername());
        return userInfoPO;
    }

    private CheckOrderDTO buildCheckOrderDTO(RobParamsOtherDTO robParamsOtherDTO) {
        CheckOrderDTO checkOrderDTO = new CheckOrderDTO();
        checkOrderDTO.setCancelFlag("2");
        checkOrderDTO.setBedLevelOrderNum("000000000000000000000000000000");
        checkOrderDTO.setPassengerTicketStr(robParamsOtherDTO.getPassengerTicketStr());
        checkOrderDTO.setOldPassengerStr(robParamsOtherDTO.getOldPassengerStr());
        checkOrderDTO.setRandCode(robParamsOtherDTO.getRandCode());
        checkOrderDTO.setWhatsSelect(robParamsOtherDTO.getWhatsSelect());
        return checkOrderDTO;
    }

    private QueueCountDTO buildQueueCountDTO(RobParamsOtherDTO robParamsOtherDTO) {
        QueueCountDTO queueCountDTO = new QueueCountDTO();
        queueCountDTO.setSeatType(robParamsOtherDTO.getSeatType());
        return queueCountDTO;
    }

    private DoOrderDTO buildDoOrderDTO(RobParamsOtherDTO robParamsOtherDTO) {
        DoOrderDTO doOrderDTO = new DoOrderDTO();
        doOrderDTO.setPassengerTicketStr(robParamsOtherDTO.getPassengerTicketStr());
        doOrderDTO.setOldPassengerStr(robParamsOtherDTO.getOldPassengerStr());
        doOrderDTO.setRandCode(robParamsOtherDTO.getRandCode());
        doOrderDTO.setSeatDetailType(robParamsOtherDTO.getSeatDetailType());
        doOrderDTO.setWhatsSelect(robParamsOtherDTO.getWhatsSelect());
        doOrderDTO.setRoomType("00");
        doOrderDTO.setDwAll("N");
        doOrderDTO.setChooseSeats("1F");
        return doOrderDTO;
    }

    @Override
    public Boolean doRob(String payload) {
        logger.info("尝试抢票，入参：{}", payload);
        JSONObject jsonObject = JSON.parseObject(payload);
        boolean isLogin = loginService.keepLogin();
        boolean isSuccess = false;
        if (isLogin) {
            isSuccess = doRobBySeatType(jsonObject);
        } else {
            UserInfoPO userInfoPO = JSON.parseObject(jsonObject.getString("userInfoPO"), UserInfoPO.class);
            Boolean isAutoLogin = loginService.autoLogin(userInfoPO);
            if (isAutoLogin == null) {
                // 密码或账号错误
                throw new RobException();
            }
            if (isAutoLogin) {
                isAutoLogin = loginService.keepLogin();
                if (isAutoLogin) {
                    isSuccess = doRobBySeatType(jsonObject);
                }
            }
        }
        if (!isSuccess) {
            logger.info("抢票失败");
        }
        return isSuccess;
    }

    /*---------------------------------------------------START: 抢票流程------------------------------------------------------*/
    private Boolean doRobBySeatType(JSONObject jsonObject) {
        CheckOrderDTO checkOrderDTO = JSON.parseObject(jsonObject.getString("checkOrderData"), CheckOrderDTO.class);
        QueueCountDTO queueCountDTO = JSON.parseObject(jsonObject.getString("queueCountData"), QueueCountDTO.class);
        DoOrderDTO doOrderDTO = JSON.parseObject(jsonObject.getString("doOrderData"), DoOrderDTO.class);
        RobParamsDTO robParamsDTO = JSON.parseObject(jsonObject.getString("robParamsData"), RobParamsDTO.class);
        boolean isSuccess = checkAllDateSecretStr(robParamsDTO, checkOrderDTO, queueCountDTO, doOrderDTO);
        return isSuccess;
    }

    private Boolean checkAllDateSecretStr(RobParamsDTO robParamsDTO, CheckOrderDTO checkOrderDTO,
                                          QueueCountDTO queueCountDTO, DoOrderDTO doOrderDTO) {
        Date limitDate = getSpecifiedDate(-1);
        boolean isValid = false;
        int dateCount = 0;
        String[] trainDates = robParamsDTO.getTrainDate().split(",");
        for (String trainDate : trainDates) {
            Date queryDate = formatStringToDate(trainDate);
            // 若出发日期全部过期则终止任务
            if (!isValid) {
                isValid = limitDate.before(queryDate);
                if (!isValid) {
                    if (++ dateCount == trainDates.length) {
                        throw new RobException();
                    }
                    continue;
                }
                isValid = false;
            }
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

    private Date formatStringToDate(String trainDate) {
        Date queryDate = null;
        try {
            queryDate = formatter.parse(trainDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return queryDate;
    }

    private Date getSpecifiedDate(int amount) {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DATE, amount); // 正负代表往后往前
        return calendar.getTime();
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

        boolean isSuccess = false;
        boolean isAllSeatType = checkAllSeatType(checkOrderDTO);
        if (isAllSeatType) {
            List<String> seatTypeList = chooseSeatTypeList(robParamsDTO);
            for (String seatType : seatTypeList) {
                changeSeatType(seatType, checkOrderDTO, queueCountDTO, doOrderDTO);
                isSuccess = submitOrderByRobNoSeat(checkOrderDTO, queueCountDTO, robParamsDTO,
                        doOrderDTO, orderParamsMap, ticketInfoDTO, globalRepeatSubmitToken);
                if (isSuccess) {
                    return true;
                }
            }
            changeSeatType("all", checkOrderDTO, queueCountDTO, doOrderDTO);
        } else {
           isSuccess = submitOrderByRobNoSeat(checkOrderDTO, queueCountDTO, robParamsDTO,
                    doOrderDTO, orderParamsMap, ticketInfoDTO, globalRepeatSubmitToken);
        }
        return isSuccess;
    }

    private Boolean submitOrderByRobNoSeat(CheckOrderDTO checkOrderDTO, QueueCountDTO queueCountDTO,
                                RobParamsDTO robParamsDTO, DoOrderDTO doOrderDTO,
                                Map<String, String> orderParamsMap,
                                TicketInfoDTO ticketInfoDTO, String globalRepeatSubmitToken) {
        boolean robNoSeat = robParamsDTO.getRobNoSeat();
        if (!robNoSeat) {
            // // 验证订单太快会被12306拒绝，要间隔一秒以上；用页面获取到的参数判断余票；获取队列计数就不需要了，也不影响下单
            QueryLeftNewDetailDTO queryLeftNewDetailDTO = ticketInfoDTO.getQueryLeftNewDetailDTO();
            boolean isSeatAvailable = checkSeatAvailable(queryLeftNewDetailDTO, queueCountDTO);
            if (!isSeatAvailable) {
                return false;
            }
        }
        boolean isCheck = checkOrder(buildCheckOrderData(checkOrderDTO, ticketInfoDTO, globalRepeatSubmitToken));
        if (isCheck) {
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
        String trainNumber = robParamsDTO.getTrainNumber();
        String[] trainNumbers = !StringUtils.isEmpty(trainNumber) ? trainNumber.split(",") : null;
        for (Object ticket : tickets) {
            String[] attrs = ticket.toString().split("\\|");
            String secretStr = attrs[SECRET_INDEX];
            if (!StringUtils.isEmpty(secretStr)) {
                filterQueryResultByTrainDate(robParamsDTO, trainNumbers, attrs, secretStrList);
            }
        }
    }

    private void filterQueryResultByTrainDate(RobParamsDTO robParamsDTO, String[] trainNumbers,
                                              String[] attrs, List<String> secretStrList) {
        String leftTime = attrs[LEFT_TIME_INDEX];
        if (compareTime(leftTime, robParamsDTO.getLeftTimeBegin()) &&
                !compareTime(leftTime, robParamsDTO.getLeftTimeEnd())) {
            filterQueryResultByTrainNumbers(trainNumbers, attrs, secretStrList);
        }
    }

    private void filterQueryResultByTrainNumbers(String[] trainNumbers, String[] attrs, List<String> secretStrList) {
        String shortNum = attrs[SHORT_TRAIN_NUM_INDEX];
        String secretStr = attrs[SECRET_INDEX];
        if (!ObjectUtils.isEmpty(trainNumbers)) {
            String longNum = attrs[LONG_TRAIN_NUM_INDEX];
            for (String no : trainNumbers) {
                if (shortNum.equals(no) || longNum.equals(no)) {
                    secretStrList.add(secretStr);
                    logger.info("添加secretStr成功，车次：{}", no);
                    return;
                }
            }
        } else {
            secretStrList.add(secretStr);
            logger.info("添加secretStr成功，车次：{}", shortNum);
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

    private boolean checkSeatAvailable(QueryLeftNewDetailDTO queryLeftNewDetailDTO, QueueCountDTO queueCountDTO) {
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(queryLeftNewDetailDTO));
        int ticketNum = Integer.valueOf(jsonObject.getString(queueCountDTO.getSeatType()));
        if (ticketNum > 0) {
            logger.info("余票剩余：{} 张", ticketNum);
            return true;
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
