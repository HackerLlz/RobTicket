package com.example.frp.entity.DTO.robProcess;

import lombok.Data;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 18:28
 */
/**
 * @ParamAlias("train_date")  自定义的Controkler实体参数的字段转化, 但模板引擎会无效
 * @DateTimeFormat(pattern="yyyy-MM-dd")   Controkler实体参数的日期格式
 * @JSONField(name="train_date")
 * @JsonFormat(pattern="yyyy-MM-dd")
 */
@Data
public class RobParamsDTO {
    private Boolean robNoSeat;

    private String fromStation;

    private String fromStationName;

    private String toStation;

    private String toStationName;

    private String trainDate;

    private String leftTimeBegin;

    private String leftTimeEnd;

    private String trainNumber;

    private String purposeCodes;

    private String tourFlag;
}
