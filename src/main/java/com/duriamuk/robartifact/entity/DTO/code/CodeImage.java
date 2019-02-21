package com.duriamuk.robartifact.entity.DTO.code;

import lombok.Data;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-02-18 15:58
 */
@Data
public class CodeImage {
    private Long id;

    private String image;

    private String ocrResult;

    private String answer;

    private String hash;
}
