package com.duriamuk.robartifact.entity.DTO.robProcess;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 17:39
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueueCountDTO {
    @JSONField(name = "seatType")
    private String seatType;

    /*---------------------------------------------------START: OrderSubmitDTO------------------------------------------------------*/
    @JSONField(name = "train_date")
    private String trainDate;

    @JSONField(name = "train_no")
    private String trainNo;

    @JSONField(name = "stationTrainCode")
    private String stationTrainCode;

    @JSONField(name = "fromStationTelecode")
    private String fromStationTelecode;

    @JSONField(name = "toStationTelecode")
    private String toStationTelecode;
    /*---------------------------------------------------END: OrderSubmitDTO--------------------------------------------------------*/

    /*---------------------------------------------------START: TicketInfoDTO------------------------------------------------------*/
    @JSONField(name = "leftTicket")
    private String leftTicket;

    @JSONField(name = "purpose_codes")
    private String purposeCodes;

    @JSONField(name = "train_location")
    private String trainLocation;
    /*---------------------------------------------------END: TicketInfoDTO--------------------------------------------------------*/

    @JSONField(name = "REPEAT_SUBMIT_TOKEN")
    private String globalRepeatSubmitToken;
}
