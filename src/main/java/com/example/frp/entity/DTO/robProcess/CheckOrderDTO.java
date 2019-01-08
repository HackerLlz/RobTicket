package com.example.frp.entity.DTO.robProcess;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 17:23
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckOrderDTO {
    @JSONField(name = "cancel_flag")
    private String cancelFlag;

    @JSONField(name = "bed_level_order_num")
    private String bedLevelOrderNum;

    @JSONField(name = "randCode")
    private String randCode;

    @JSONField(name = "whatsSelect")
    private String whatsSelect;

    @JSONField(name = "passengerTicketStr")
    private String passengerTicketStr;

    @JSONField(name = "oldPassengerStr")
    private String oldPassengerStr;

    /*---------------------------------------------------START: OrderSubmitDTO------------------------------------------------------*/
    @JSONField(name = "tour_flag")
    private String tourFlag;
    /*---------------------------------------------------END: OrderSubmitDTO--------------------------------------------------------*/

    @JSONField(name = "REPEAT_SUBMIT_TOKEN")
    private String globalRepeatSubmitToken;
}
