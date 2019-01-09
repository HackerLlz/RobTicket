package com.duriamuk.robartifact.entity.DTO.robProcess;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 17:16
 */
@Data
public class OrderSubmitDTO {
    @JSONField(name = "secretStr")
    private String secretStr;

    @JSONField(name = "train_date")
    private String trainDate;

    @JSONField(name = "back_train_date")
    private String backTrainDate;

    @JSONField(name = "tour_flag")
    private String tourFlag;

    @JSONField(name = "purpose_codes")
    private String purposeCodes;

    @JSONField(name = "query_from_station_name")
    private String queryFromStationName;

    @JSONField(name = "query_to_station_name")
    private String queryToStationName;
}
