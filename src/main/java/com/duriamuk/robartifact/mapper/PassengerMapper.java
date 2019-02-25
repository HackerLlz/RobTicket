package com.duriamuk.robartifact.mapper;

import com.duriamuk.robartifact.entity.PO.passenger.PassengerPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-02-24 21:34
 */
@Mapper
public interface PassengerMapper {
    Integer insertPassengerList(List<PassengerPO> list);

    List<PassengerPO> listPassengerByUsername(String username);
}
