package com.duriamuk.robartifact.service;

import com.duriamuk.robartifact.entity.DTO.robProcess.RobParamsDTO;

import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-02 17:55
 */
public interface RobService {
    Boolean doRob(String payload) throws Exception;

    Boolean insertRobRecord(RobParamsDTO robParamsDTO);

    List<RobParamsDTO> listRobRecordByUserId(Long userId);

    RobParamsDTO getRobRecordById(Long id);

    void deleteRobRecordById(Long id);

    void updateRobRecord(RobParamsDTO robParamsDTO);
}
