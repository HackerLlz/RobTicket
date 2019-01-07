package com.example.frp.entity.DTO.robProcess;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 17:14
 */
@Data
public class QueryDTO {
    @JSONField(name = "leftTicketDTO.train_date")
    private String trainDate;

    @JSONField(name = "leftTicketDTO.from_station")
    private String fromStation;

    @JSONField(name = "leftTicketDTO.to_station")
    private String toStation;

    @JSONField(name = "purpose_codes")
    private String purposeCodes;
}
