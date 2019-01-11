package com.duriamuk.robartifact.mapper;

import com.duriamuk.robartifact.entity.DTO.robProcess.RobParamsDTO;

import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-11 14:16
 */
public interface RobMapper {
    void insertRobRecord(RobParamsDTO robParamsDTO);

    List<RobParamsDTO> listRobRecordByUserId(Long userId);

    RobParamsDTO getRobRecordById(Long id);

    void deleteRobRecordById(Long id);

    void updateRobRecord(RobParamsDTO robParamsDTO);
}
