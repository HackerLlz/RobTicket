package com.duriamuk.robartifact.entity.DTO.robProcess;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 18:28
 */

/**
 * @ParamAlias("train_date") 自定义的Controkler实体参数的字段转化, 但模板引擎会无效
 * @DateTimeFormat(pattern="yyyy-MM-dd") Controkler实体参数的日期格式
 * @JSONField(name="train_date")
 * @JsonFormat(pattern="yyyy-MM-dd")
 */
@Data
public class RobParamsDTO {
    private Long id;

    private Long userId;

    private Boolean robNoSeat;

    @NotBlank(message = "缺少出发站编码")
    private String fromStation;

    @NotBlank(message = "缺少到达站编码")
    private String fromStationName;

    @NotBlank(message = "缺少出发站名称")
    private String toStation;

    @NotBlank(message = "缺少到达站名称")
    private String toStationName;

    @NotBlank(message = "缺少列车日期")
    private String trainDate;

    @NotBlank(message = "缺少出发时间")
    private String leftTimeBegin;

    @NotBlank(message = "缺少到达时间")
    private String leftTimeEnd;

    private String trainNumber;

    @NotBlank(message = "缺少车票类型")
    private String purposeCodes;

    @NotBlank(message = "缺少旅程类型")
    private String tourFlag;

    private Integer status;
}
