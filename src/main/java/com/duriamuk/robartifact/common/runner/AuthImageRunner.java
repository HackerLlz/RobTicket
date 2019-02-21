package com.duriamuk.robartifact.common.runner;

import com.duriamuk.robartifact.common.constant.ArrayConstant;
import com.duriamuk.robartifact.common.constant.PrefixName;
import com.duriamuk.robartifact.common.tool.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-28 15:37
 */
//@Component
@Order(value = 1)
public class AuthImageRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(AuthImageRunner.class);
    private static final String IMAGE_PATH = "static/images/authImage/";
    private static final int INIT_CAPACITY = 40;

    @Override
    public void run(ApplicationArguments arguments) {
        logger.info("开始加载验证码切割图");
        int count = 0;
        for (String text : ArrayConstant.CODE_TEXT_LIST) {
            File file = new File(getRealPath(text));
            if (file.isDirectory()) {
                File[] subFiles = file.listFiles();
                List<String> codes = new ArrayList<>(INIT_CAPACITY);
                for (File f : subFiles) {
                    String base64Code = Base64.getEncoder().encodeToString(imageToBytes(f));
                    codes.add(base64Code);
                }
                RedisUtils.set(PrefixName.AUTH_IMAGE + text, codes);
                logger.info("{}. {} 加载成功", ++count, text);
            }
        }
        logger.info("成功加载验证码切割图");
    }

    private String getRealPath(String text) {
        // 服务器部署可能出现中文路径错误
        String path = AuthImageRunner.class.getClassLoader().getResource(IMAGE_PATH + text).getPath();
        try {
            path = URLDecoder.decode(path, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return path;
    }

    private byte[] imageToBytes(File file) {
        byte[] bytes = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bytes = byteArrayOutputStream.toByteArray();
        return bytes;
    }
}
