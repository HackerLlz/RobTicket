package com.duriamuk.robartifact.entity.PO.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-09 17:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoPO {
    private String username;

    private String sendMail;
}
