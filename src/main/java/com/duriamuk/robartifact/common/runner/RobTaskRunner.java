package com.duriamuk.robartifact.common.runner;

import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.constant.PrefixName;
import com.duriamuk.robartifact.common.schedule.RobScheduledThreadPool;
import com.duriamuk.robartifact.common.schedule.RobTask;
import com.duriamuk.robartifact.common.tool.RedisUtils;
import com.duriamuk.robartifact.entity.DTO.robProcess.*;
import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;
import com.duriamuk.robartifact.mapper.UserMapper;
import com.duriamuk.robartifact.service.RobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-02-25 14:21
 */
@Component
@Order(value = 1)
public class RobTaskRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(RobTaskRunner.class);

    @Autowired
    private RobService robService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(ApplicationArguments arguments) {
        logger.info("开始重启抢票任务");
        List<RobParamsDTO> robParamsDTOList = robService.listRobRecordWithOther(buildRobParamsDTO());
        for (RobParamsDTO rob : robParamsDTOList) {
            long id = rob.getId();
            long userId = rob.getUserId();
            RedisUtils.setWithExpire(PrefixName.TABLE_ROB_RECORD + id, true, 30, TimeUnit.DAYS);
            RobScheduledThreadPool.schedule(new RobTask(buildPayload(rob), 1, id, userId));
            logger.info("重启抢票任务userId:{};id:{}", userId, id);
        }
        logger.info("重启抢票任务成功");
    }

    private RobParamsDTO buildRobParamsDTO() {
        RobParamsDTO robParamsDTO = new RobParamsDTO();
        robParamsDTO.setStatus(1);
        return robParamsDTO;
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
}
