package com.duriamuk.robartifact.mapper;

import com.duriamuk.robartifact.entity.DTO.robProcess.RobParamsDTO;
import com.duriamuk.robartifact.entity.DTO.robProcess.RobParamsOtherDTO;

import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-11 14:16
 */
public interface RobMapper {
    void insertRobRecord(RobParamsDTO robParamsDTO);

    Integer insertRobRecordOther(RobParamsOtherDTO robParamsOtherDTO);

    List<RobParamsDTO> listRobRecordByUserId(Long userId);

    List<RobParamsDTO> listRobRecordWithOther(RobParamsDTO robParamsDTO);

    RobParamsDTO getRobRecordById(Long id);

    void deleteRobRecordById(Long id);

    void updateRobRecord(RobParamsDTO robParamsDTO);
}
