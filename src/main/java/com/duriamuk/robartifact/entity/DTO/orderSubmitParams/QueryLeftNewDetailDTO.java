package com.duriamuk.robartifact.entity.DTO.orderSubmitParams;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-02-27 10:46
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryLeftNewDetailDTO {
    private String ZE_num;

    private String ZY_num;

    private String SWZ_num;

    private String YZ_num;

    private String YW_num;

    private String RW_num;

    @JSONField(name = "O" )
    public String getZE_num() {
        return ZE_num;
    }

    public void setZE_num(String ZE_num) {
        this.ZE_num = ZE_num;
    }

    @JSONField(name = "M")
    public String getZY_num() {
        return ZY_num;
    }

    public void setZY_num(String ZY_num) {
        this.ZY_num = ZY_num;
    }

    @JSONField(name = "9")
    public String getSWZ_num() {
        return SWZ_num;
    }

    public void setSWZ_num(String SWZ_num) {
        this.SWZ_num = SWZ_num;
    }

    @JSONField(name = "1")
    public String getYZ_num() {
        return YZ_num;
    }

    public void setYZ_num(String YZ_num) {
        this.YZ_num = YZ_num;
    }

    @JSONField(name = "3")
    public String getYW_num() {
        return YW_num;
    }

    public void setYW_num(String YW_num) {
        this.YW_num = YW_num;
    }

    @JSONField(name = "4")
    public String getRW_num() {
        return RW_num;
    }

    public void setRW_num(String RW_num) {
        this.RW_num = RW_num;
    }
}
