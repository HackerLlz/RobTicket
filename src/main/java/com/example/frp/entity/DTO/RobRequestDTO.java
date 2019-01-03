package com.example.frp.entity.DTO;


import com.alibaba.fastjson.annotation.JSONField;
import com.example.frp.common.paramAlias.ParamAlias;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author: DuriaMuk
 * @description: 出发车票DTO
 * @create: 2018-12-14 15:43
 * @ParamAlias("train_date")  自定义的Controkler实体参数的字段转化, 但模板引擎会无效
 * @DateTimeFormat(pattern="yyyy-MM-dd")   Controkler实体参数的日期格式
 * @JSONField(name="train_date")
 * @JsonFormat(pattern="yyyy-MM-dd")
 */
@Data
public class RobRequestDTO {
    private String secretStr;

    private String trainDate;

    private String backTrainDate;

    private String tourFlag;

    private String purposeCodes;

    private String fromStation;

    private String toStation;

    private String fromStationCode;

    private String toStationCode;

    private String trainNumber;
}
