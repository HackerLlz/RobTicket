package com.duriamuk.robartifact.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.tool.ImageHashUtils;
import com.duriamuk.robartifact.entity.DTO.code.CodeImage;
import com.duriamuk.robartifact.mapper.CodeImageMapper;
import com.duriamuk.robartifact.service.AuthImageService;
import com.duriamuk.robartifact.service.CodeCrackService;
import com.duriamuk.robartifact.service.LoginService;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-02-18 15:16
 */
@Service("codeCrackService")
public class CodeCrackServiceImpl implements CodeCrackService {
    private static final Logger logger = LoggerFactory.getLogger(CodeCrackServiceImpl.class);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH时mm分ss秒SSSS");

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
        String result = loginService.getCode();
        if (result.startsWith("{")) {
            JSONObject resultJson = JSON.parseObject(result);
            if (resultJson.getInteger("result_code") == 0) {
                String image = resultJson.getString("image");
                byte[] bytes = Base64.getDecoder().decode(image);
                String ocrResult = authImageService.doOCR(bytes);
                if (!StringUtils.isEmpty(ocrResult)) {
                    CodeImage codeImage = new CodeImage();
                    codeImage.setImage(image);
                    codeImage.setOcrResult(ocrResult);
                    return codeImage;
                }
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
            int row = y > 79 ? 1 : 0;
            int col = x / (1 + 73);
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

    private String getImageHash(byte[] bytes, int row, int col) {
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = Thumbnails.of(new ByteArrayInputStream(bytes))
                    .sourceRegion(5 + (67 + 5) * col, 41 + (67 + 5) * row, 67, 67)
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
                    .sourceRegion(5 + (67 + 5) * col, 41 + (67 + 5) * row, 67, 67)
                    .scale(1.0)
                    .toFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
