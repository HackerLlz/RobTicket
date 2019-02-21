package com.duriamuk.robartifact.mapper;

import com.duriamuk.robartifact.entity.DTO.code.CodeImage;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-02-19 10:45
 */
public interface CodeImageMapper {
    Integer insertCodeImage(CodeImage codeImage);

    Integer countCodeImage(CodeImage codeImage);
}
