package com.duriamuk.robartifact.entity.DTO.orderSubmitParams;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 16:52
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TicketInfoDTO {
    @JSONField(name = "tour_flag")
    private String tourFlag;

    @JSONField(name = "leftTicketStr")
    private String leftTicketStr;

    @JSONField(name = "purpose_codes")
    private String purposeCodes;

    @JSONField(name = "train_location")
    private String trainLocation;

    @JSONField(name = "key_check_isChange")
    private String keyCheckIsChange;
}
