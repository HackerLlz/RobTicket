package com.duriamuk.robartifact.entity.DTO.robProcess;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-02-26 10:13
 */
@Data
@AllArgsConstructor
public class SecretStrDTO {
    private String trainDate;

    private List<String> secretStrList;
}
