package com.duriamuk.robartifact.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.tool.HttpUtils;
import com.duriamuk.robartifact.common.tool.ImageHashUtils;
import com.duriamuk.robartifact.entity.DTO.code.CodeImage;
import com.duriamuk.robartifact.mapper.CodeImageMapper;
import com.duriamuk.robartifact.service.AuthImageService;
import com.duriamuk.robartifact.service.CodeCrackService;
import com.duriamuk.robartifact.service.LoginService;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-02-18 15:16
 */
@Service("codeCrackService")
public class CodeCrackServiceImpl implements CodeCrackService {
    private static final Logger logger = LoggerFactory.getLogger(CodeCrackServiceImpl.class);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH时mm分ss秒SSSS");
    private static final List<String> answerList = Arrays.asList("42,46", "115,48", "188,46", "260,43", "37,117", "117,117", "183,117", "258,116");
    // 图片像素
    private static final int SPACE_LENGTH = 5;
    private static final int SIDE_LENGTH = 67;
    private static final int TOP_POSITION = 41;
    // 点击坐标
    private static final int VERTICAL_CEMTER = 79;
    private static final int HORIZONTAL_DISTANCE = 1 + 73;

    @Value("${constant.codeImagePath}")
    private String codeImagePath;

    @Autowired
    private LoginService loginService;

    @Autowired
    private AuthImageService authImageService;

    @Autowired
    private CodeImageMapper codeImageMapper;

    @Override
    public CodeImage getOCRCode() {
        logger.info("获得OCR成功的验证码");
        String image = getImageCode();
        if (!StringUtils.isEmpty(image)) {
            byte[] bytes = Base64.getDecoder().decode(image);
            String ocrResult = authImageService.doOCR(bytes);
            if (!StringUtils.isEmpty(ocrResult)) {
                CodeImage codeImage = new CodeImage();
                codeImage.setImage(image);
                codeImage.setOcrResult(ocrResult);
                return codeImage;
            }
        }
        return null;
    }

    @Override
    public Integer addCodeImage(CodeImage codeImage) {
        String[] answers = codeImage.getAnswer().split(",");
        byte[] bytes = Base64.getDecoder().decode(codeImage.getImage());
        int totalInsertCount = 0;
        for (int i = 0; i < answers.length; i += 2) {
            int x = Integer.valueOf(answers[i]);
            int y = Integer.valueOf(answers[i + 1]);
            int row = y > VERTICAL_CEMTER? 1 : 0;
            int col = x / HORIZONTAL_DISTANCE;
            codeImage.setHash(getImageHash(bytes, row, col));
            int insertCount = codeImageMapper.insertCodeImage(codeImage);
            logger.info("{}", insertCount == 1 ? "成功插入验证图片" : "验证图片已存在");
            if (insertCount == 1) {
                saveCodeImage(codeImage, bytes, row, col);
                totalInsertCount ++;
            }
        }
        logger.info("总共插入图片 {} 张", totalInsertCount);
        return totalInsertCount;
    }

    @Override
    public Boolean crackCode() {
        logger.info("破解验证码");
        String image = getImageCode();
        if (!StringUtils.isEmpty(image)) {
            byte[] bytes = Base64.getDecoder().decode(image);
            String ocrResult = authImageService.doOCR(bytes);
            if (!StringUtils.isEmpty(ocrResult)) {
                String result = loginService.checkCode(buildAnswer(bytes, ocrResult));
                if (result.startsWith("{") &&
                        JSON.parseObject(result).getInteger("result_code") == 4) {
                    logger.info("验证码破解成功");
                    return true;
                }
            }
        }
        logger.info("验证码破解失败");
        return false;
    }

    private String getImageCode() {
        String result = loginService.getCode();
        if (result.startsWith("{")) {
            JSONObject resultJson = JSON.parseObject(result);
            if (resultJson.getInteger("result_code") == 0) {
                return resultJson.getString("image");
            }
        }
        return null;
    }

    private String getImageHash(byte[] bytes, int row, int col) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = Thumbnails.of(new ByteArrayInputStream(bytes))
                    .sourceRegion(SPACE_LENGTH + (SIDE_LENGTH + SPACE_LENGTH) * col,
                            TOP_POSITION + (SIDE_LENGTH + SPACE_LENGTH) * row,
                            SIDE_LENGTH,
                            SIDE_LENGTH)
                    .scale(1.0)
                    .asBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ImageHashUtils.getHash(bufferedImage);
    }

    private void saveCodeImage(CodeImage codeImage, byte[] bytes, int row, int col) {
        String path = codeImagePath + codeImage.getOcrResult() ;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        path += "\\" + formatter.format(new Date());
        try {
            Thumbnails.of(new ByteArrayInputStream(bytes))
                    .sourceRegion(SPACE_LENGTH + (SIDE_LENGTH + SPACE_LENGTH) * col,
                            TOP_POSITION + (SIDE_LENGTH + SPACE_LENGTH) * row,
                            SIDE_LENGTH,
                            SIDE_LENGTH)
                    .scale(1.0)
                    .toFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildAnswer(byte[] bytes, String ocrResult) {
        CodeImage codeImage = new CodeImage();
        codeImage.setOcrResult(ocrResult);
        StringBuffer answerSb = new StringBuffer();
        for (int n = 0; n < 8; n ++) {
            int row = n < 4 ? 0 : 1;
            int col = n < 4 ? n : n - 4;
            codeImage.setHash(getImageHash(bytes, row, col));
            int count = codeImageMapper.countCodeImage(codeImage);
            if (count > 0) {
                logger.info("图片匹配成功");
                answerSb.append(",").append(answerList.get(n));
            } else {
                logger.info("图片匹配失败");
            }
        }
        return answerSb.replace(0, 1, "").toString();
    }
}
