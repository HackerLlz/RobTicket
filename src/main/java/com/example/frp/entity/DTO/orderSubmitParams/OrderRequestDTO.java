package com.example.frp.entity.DTO.orderSubmitParams;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 16:45
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequestDTO {
    @JSONField(name = "train_no")
    private String trainNo;

    @JSONField(name = "station_train_code")
    private String stationTrainCode;

    @JSONField(name = "from_station_telecode")
    private String fromStationTelecode;

    @JSONField(name = "to_station_telecode")
    private String toStationTelecode;

    @JSONField(name = "train_date")
    private TrainDate trainDate;
}
