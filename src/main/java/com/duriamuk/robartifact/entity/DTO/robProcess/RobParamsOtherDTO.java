package com.duriamuk.robartifact.entity.DTO.robProcess;

import lombok.Data;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-02-25 15:32
 */
@Data
public class RobParamsOtherDTO {
    private Long id;

    private Long robId;

    private String passengerTicketStr;

    private String oldPassengerStr;

    private String randCode;

    private String whatsSelect;

    private String seatType;

    private String seatDetailType;
}
