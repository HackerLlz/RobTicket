package com.duriamuk.robartifact.common.runner;

import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.constant.PrefixName;
import com.duriamuk.robartifact.common.constant.ValueConstant;
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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${constant.taskRunnerEnable}")
    private Boolean taskRunnerEnable;

    @Autowired
    private RobService robService;

    @Override
    public void run(ApplicationArguments arguments) {
        logger.info("开始重启抢票任务");
        if (taskRunnerEnable) {
            robService.restartTask(buildRobParamsDTO());
            logger.info("重启抢票任务成功");
        } else {
            logger.info("重启抢票任务关闭");
        }
    }

    private RobParamsDTO buildRobParamsDTO() {
        RobParamsDTO robParamsDTO = new RobParamsDTO();
        robParamsDTO.setStatus(1);
        return robParamsDTO;
    }


}
