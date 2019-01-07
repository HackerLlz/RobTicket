package com.example.frp.entity.DTO.robProcess;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 17:40
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoOrderDTO {
    @JSONField(name = "passengerTicketStr")
    private String passengerTicketStr;

    @JSONField(name = "oldPassengerStr")
    private String oldPassengerStr;

    @JSONField(name = "randCode")
    private String randCode;

    @JSONField(name = "choose_seats")
    private String chooseSeats;

    @JSONField(name = "seatDetailType")
    private String seatDetailType;

    @JSONField(name = "whatsSelect")
    private String whatsSelect;

    @JSONField(name = "roomType")
    private String roomType;

    @JSONField(name = "dwAll")
    private String dwAll;

    /*---------------------------------------------------START: TicketInfoDTO------------------------------------------------------*/
    @JSONField(name = "purpose_codes")
    private String purposeCodes;

    @JSONField(name = "key_check_isChange")
    private String keyCheckIsChange;

    @JSONField(name = "leftTicketStr")
    private String leftTicketStr;

    @JSONField(name = "train_location")
    private String trainLocation;
    /*---------------------------------------------------END: TicketInfoDTO--------------------------------------------------------*/
}
