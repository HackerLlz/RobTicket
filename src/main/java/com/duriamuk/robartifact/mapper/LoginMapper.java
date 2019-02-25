package com.duriamuk.robartifact.mapper;

import com.duriamuk.robartifact.entity.PO.user.UserInfoPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-09 16:29
 */
@Mapper
public interface LoginMapper {
    Integer insertUsername(String username);

    UserInfoPO getLoginInfoByUsername(String username);
}
