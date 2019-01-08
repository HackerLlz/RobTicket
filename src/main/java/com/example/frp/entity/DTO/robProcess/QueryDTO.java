package com.example.frp.entity.DTO.robProcess;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import lombok.Data;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-07 17:14
 */
@Data
//@JSONType(orders = {"id","name"})
public class QueryDTO {
    @JSONField(name = "leftTicketDTO.train_date", ordinal = 1)
    private String trainDate;

    @JSONField(name = "leftTicketDTO.from_station", ordinal = 2)
    private String fromStation;

    @JSONField(name = "leftTicketDTO.to_station", ordinal = 3)
    private String toStation;

    @JSONField(name = "purpose_codes", ordinal = 4)
    private String purposeCodes;
}
