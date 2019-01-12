package com.duriamuk.robartifact.entity.PO.AuthCode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-12 17:45
 */
@Data
@NoArgsConstructor
public class AuthCodePO {
    private Long id;

    private String md5;

    private Integer checkPosition;

    private Integer status;

    public AuthCodePO(String md5, Integer checkPosition, Integer status) {
        this.md5 = md5;
        this.checkPosition = checkPosition;
        this.status = status;
    }
}
