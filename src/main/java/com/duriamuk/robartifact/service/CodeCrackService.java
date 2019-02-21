package com.duriamuk.robartifact.service;

import com.duriamuk.robartifact.entity.DTO.code.CodeImage;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-02-18 15:15
 */
public interface CodeCrackService {
    CodeImage getOCRCode();

    Integer addCodeImage(CodeImage codeImage);
}
