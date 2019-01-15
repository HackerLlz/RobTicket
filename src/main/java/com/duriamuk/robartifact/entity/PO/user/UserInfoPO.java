package com.duriamuk.robartifact.entity.PO.user;

import com.duriamuk.robartifact.common.validate.group.Insert;
import com.duriamuk.robartifact.common.validate.group.Update;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-09 17:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoPO {
    private Long id;

    private String username;

    @Email(message = "邮箱格式不正确", groups = {Update.class})
    private String sendMail;
}
