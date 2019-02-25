package com.duriamuk.robartifact.service;

import com.duriamuk.robartifact.entity.DTO.robProcess.RobParamsDTO;
import com.duriamuk.robartifact.entity.DTO.robProcess.RobParamsOtherDTO;

import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-02 17:55
 */
public interface RobService {
    Boolean doRob(String payload) throws Exception;

    Boolean insertRobRecord(RobParamsDTO robParamsDTO, RobParamsOtherDTO robParamsOtherDTO);

    List<RobParamsDTO> listRobRecordByUserId(Long userId);

    List<RobParamsDTO> listRobRecordWithOther(RobParamsDTO robParamsDTO);

    RobParamsDTO getRobRecordById(Long id);

    void deleteRobRecordById(Long id);

    void updateRobRecord(RobParamsDTO robParamsDTO);
}
