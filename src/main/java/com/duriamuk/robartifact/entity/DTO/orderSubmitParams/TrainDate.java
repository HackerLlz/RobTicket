package com.duriamuk.robartifact.entity.DTO.orderSubmitParams;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 16:50
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrainDate {
    @JSONField(name = "time")
    private Long time;
}
