package com.duriamuk.robartifact.entity.PO.passenger;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-02-24 21:36
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PassengerPO {
    private Long id;

    private String username;

    @JSONField(name = "passenger_name")
    private String passengerName;

    @JSONField(name = "passenger_id_type_code")
    private String passengerIdTypeCode;

    @JSONField(name = "passenger_id_type_name")
    private String passengerIdTypeName;

    @JSONField(name = "passenger_id_no")
    private String passengerIdNo;

    @JSONField(name = "passenger_type")
    private String passengerType;

    @JSONField(name = "passenger_flag")
    private String passengerFlag;

    @JSONField(name = "mobile_no")
    private String mobileNo;

    @JSONField(name = "first_letter")
    private String firstLetter;

    @JSONField(name = "index_id")
    private String indexId;

    @JSONField(name = "total_times")
    private String totalTimes;

    private String recordCount;
}
