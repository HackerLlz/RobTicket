package com.duriamuk.robartifact.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.duriamuk.robartifact.common.constant.ArrayConstant;
import com.duriamuk.robartifact.common.constant.PrefixName;
import com.duriamuk.robartifact.common.tool.*;
import com.duriamuk.robartifact.service.AuthImageService;
import com.duriamuk.robartifact.service.LoginService;
import net.coobird.thumbnailator.Thumbnails;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import org.apache.commons.codec.digest.DigestUtils;
import org.opencv.core.*;
import org.opencv.features2d.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.thymeleaf.util.ListUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-22 11:11
 */
@Service("authImageService")
public class AuthImageServiceImpl implements AuthImageService {
    private static final Logger logger = LoggerFactory.getLogger(AuthImageServiceImpl.class);
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH时mm分ss秒SSSS");
    private static final List<String> answerList = Arrays.asList("42,46", "115,48", "188,46", "260,43", "37,117", "117,117", "183,117", "258,116");
    private static final int COMPARE_LENGTH = 300;
    private static final int CLIMB_TIMES = 2000;
    private static final int MAX_LENGTH = 4;
    private static final int MATCH_COUNT = 2;
    private static final int MAX_ANSWER_NUM = 2;
    private static final int MATCH_POINT_COUNT = 9;
    private static final float NNDR_RATIO = 0.3f;  // 越大相似越严格
    private static final int TEST_TIMES = 5;


    private static final ITesseract tesseract = new Tesseract();

    static {
        tesseract.setDatapath("tessdata");  // src同级
        tesseract.setLanguage("chi");
        tesseract.setPageSegMode(8);

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Autowired
    private LoginService loginService;

    @Override
    public void climbAuthFont(String message) {
        logger.info("爬取验证码训练字体");
        for (int i = 0; i < CLIMB_TIMES; i++) {
            byte[] bytes = getAuthImageBytes();
            InputStream stream = new ByteArrayInputStream(bytes);
            if (!ObjectUtils.isEmpty(stream)) {
                try {
                    Thumbnails.of(stream)
                            .sourceRegion(117, 0, 60, 30)
                            .scale(1.0)
                            .toFile("O:\\图片\\12306\\训练字体\\训练字体3\\" + formatter.format(new Date()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // 清空cookie，模拟新请求
            ThreadLocalUtils.set(null);
        }
    }

    @Override
    public void climbAuthImage(String message) {
        logger.info("爬取验证码切割图");
        for (int i = 0; i < CLIMB_TIMES; i++) {
            byte[] bytes = getAuthImageBytes();
            if (!ObjectUtils.isEmpty(bytes)) {
                for (int n = 0; n < 8; n++) {
                    InputStream stream = new ByteArrayInputStream(bytes);
                    int row = n < 4 ? 0 : 1;
                    int column = n < 4 ? n : n - 4;
                    try {
                        Thumbnails.of(stream)
                                .sourceRegion(2 + column * 72, 37 + row * 73, 75, 73)
                                .scale(1.0)
                                .toFile("O:\\图片\\12306\\切割图\\" + formatter.format(new Date()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            ThreadLocalUtils.set(null);
        }
    }

    @Override
    public Boolean isSameImage() {
        String path3 = "O:\\图片\\12306\\图库\\a\\1550561560(1).jpg";
        String path4 = "O:\\图片\\12306\\图库\\a\\1550650363(1).jpg";
        String path1 = "O:\\图片\\12306\\图库\\a\\1.JPEG";
        String path2 = "O:\\图片\\12306\\图库\\a\\2.JPEG";
        int row1 = 1;
        int col1 = 3;
        int row2 = 1;
        int col2 = 3;
        try {
            Thumbnails.of(path3)
                    .sourceRegion(5 + (67 + 5) * col1, 41 + (67 + 5) * row1, 67, 67)
                    .scale(1.0)
                    .toFile(path1);
            Thumbnails.of(path4)
                    .sourceRegion(5 + (67 + 5) * col2, 41 + (67 + 5) * row2, 67, 67)
                    .scale(1.0)
                    .toFile(path2);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String md51 = imageFileToMd5(path1);
        String md52 = imageFileToMd5(path2);
        logger.info(md51.equals(md52)? "图片相同": "图片不同");
        return false;
    }

    private String imageFileToMd5(String path) {
        File file = new File(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        String base64Code = Base64.getEncoder().encodeToString(bytes);
        String md5 = DigestUtils.md5Hex(base64Code);
        logger.info("md5：{}", md5);
        return md5;
    }

    @Override
    public Boolean similarImage(BufferedImage template, BufferedImage original) {
//        logger.info("判断图片是否相似");
        Mat templateMat = buildImageMat(template);
        Mat originalMat = buildImageMat(original);
        return matchImageMat(templateMat, originalMat);
    }

    private Mat buildImageMat(BufferedImage bufferedImage) {
        byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
        Mat mat = Mat.eye(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);
        return mat;
    }

    private boolean matchImageMat(Mat templateImage, Mat originalImage) {
        // 指定特征点算法
        FeatureDetector featureDetector = FeatureDetector.create(FeatureDetector.SIFT);
        DescriptorExtractor descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);
        // 提取模板图的特征点
        MatOfKeyPoint templateKeyPoints = new MatOfKeyPoint();
        MatOfKeyPoint templateDescriptors = new MatOfKeyPoint();
        featureDetector.detect(templateImage, templateKeyPoints);
        descriptorExtractor.compute(templateImage, templateKeyPoints, templateDescriptors);
        // 提取原图的特征点
        MatOfKeyPoint originalKeyPoints = new MatOfKeyPoint();
        MatOfKeyPoint originalDescriptors = new MatOfKeyPoint();
        featureDetector.detect(originalImage, originalKeyPoints);
        descriptorExtractor.compute(originalImage, originalKeyPoints, originalDescriptors);
        // 寻找最佳匹配
        // knnMatch方法的作用就是在给定特征描述集合中寻找最佳匹配
        // 使用KNN-matching算法，令K，则每个match得到两个最接近的descriptor，
        // 然后计算最接近距离和次接近距离之间的比值，当比值大于既定值时，才作为最终match
        List<MatOfDMatch> matches = new LinkedList();
        DescriptorMatcher descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.FLANNBASED);
        try {
            descriptorMatcher.knnMatch(templateDescriptors, originalDescriptors, matches, 2);
        } catch (Exception e) {
            logger.warn("匹配出错");
            return false;
        }
        // 计算匹配结果，依据distance进行筛选
        LinkedList<DMatch> goodMatchesList = new LinkedList();
        matches.forEach(match -> {
            DMatch[] dmatcharray = match.toArray();
            DMatch m1 = dmatcharray[0];
            DMatch m2 = dmatcharray[1];
            if (m1.distance <= m2.distance * NNDR_RATIO) {
                goodMatchesList.addLast(m1);
            }
        });
        int matchCount = goodMatchesList.size();
        if (matchCount >= MATCH_POINT_COUNT) {
            logger.info("匹配成功：{}", matchCount);
            return true;
        }
        logger.info("匹配失败：{}", matchCount);
        return false;
    }

    @Override
    public Boolean identifyAuthImage() {
        logger.info("验证图片验证码");
        ThreadLocalUtils.set(null);
        byte[] bytes = getAuthImageBytes();
        String ocrResult = doOCR(bytes);
        if (!StringUtils.isEmpty(ocrResult)) {
            try {
                Thumbnails.of( new ByteArrayInputStream(bytes))
                        .scale(1.0)
                        .toFile("O:\\图片\\12306\\验证图片测试\\" + formatter.format(new Date()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String answer = buildAuthAnswer(bytes, ocrResult);
            String result = loginService.checkCode(answer);
            if (result.startsWith("{")) {
                JSONObject resultJson = JSON.parseObject(result);
                int resultCode = resultJson.getInteger("result_code");
                if (resultCode == 4) {
                    logger.info("验证成功");
                    return true;
                }
            }
        }
        logger.info("验证失败");
        return false;
    }

    @Override
    public void testIdentifyAuthImage(String message) {
        int successCount = 0;
        for (int i = 0; i < TEST_TIMES; i ++) {
            boolean isSuccess = identifyAuthImage();
            if (isSuccess) {
                successCount ++;
            }
        }
        logger.info("验证成功率：{} % in {} counts", successCount / (TEST_TIMES * 1.0f) * 100, TEST_TIMES);
    }

    private byte[] getAuthImageBytes() {
        String result = loginService.getCode();
        if (result.startsWith("{")) {
            JSONObject resultJson = JSON.parseObject(result);
            if (resultJson.getInteger("result_code") == 0) {
                return Base64.getDecoder().decode(resultJson.getString("image"));
            }
        }
        return null;
    }

    @Override
    public String doOCR(byte[] bytes){
        if (!ObjectUtils.isEmpty(bytes)) {
            try {
                InputStream stream = new ByteArrayInputStream(bytes);  // 一次消费
                BufferedImage area = Thumbnails.of(stream)
                        .sourceRegion(117, 0, 60, 30)
                        .scale(1.0)
                        .asBufferedImage();
                String ocrResult = tesseract.doOCR(area);
                logger.info("OCR识别结果：{}", ocrResult);
                ocrResult = reviseOcrResult(ocrResult);
                logger.info("OCR修正结果：{}", ocrResult);
                return ocrResult;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String reviseOcrResult(String ocrResult) {
        int length = ocrResult.length();
        // 结果有个/n
        if (length - 1 > MAX_LENGTH) {
            return null;
        }
        // 常见词
        if (ocrResult.startsWith("漏鸥")) {
            return "海鸥";
        }
        // 特例
        if (ocrResult.startsWith("锣") && ocrResult.length() < 3) {
            return "锣";
        }
        // 易错
        if (ocrResult.equals("红豆")) {
            return "红豆";
        }if (ocrResult.equals("红枣")) {
            return "红枣";
        }
        for (String text : ArrayConstant.CODE_TEXT_LIST) {
            if ((text.contains(ocrResult) ||
                    matchText(text, ocrResult)) &&
                    !"锣".equals(text) && !"红豆".equals(text) && !"红枣".equals(text)) {
                return text;
            }
        }
        return null;
    }

    private boolean matchText(String text, String ocrResult) {
        char[] array = text.toCharArray();
        int count = 0;
        int matchIndex = -1;
        for (char ch : array) {
            int index = ocrResult.indexOf(String.valueOf(ch));
            if (matchIndex < index) {
                // 要按顺序包含才算
                matchIndex = index;
                count ++;
            }
            if (count == MATCH_COUNT) {
                return true;
            }
        }
        return false;
    }

    private String buildAuthAnswer(byte[] bytes, String ocrResult) {
        StringBuffer resultSb = new StringBuffer();
        StringBuffer pageSb = new StringBuffer();
        int answerNum = 0;
        List<String> codes = RedisUtils.getList(PrefixName.AUTH_IMAGE + ocrResult);
        if (!ListUtils.isEmpty(codes)) {
            for (int n = 0; n < 8; n++) {
                logger.info("验证第 {} 张图", n + 1);
                BufferedImage areaImageBuffer = getAreaImageBuffer(bytes, n);
                for (String code : codes) {
                    BufferedImage originImageBuffer = getOriginImageBuffer(code);
                    boolean isMatch = similarImage(areaImageBuffer, originImageBuffer);
                    if (isMatch) {
                        try {
                            int row = n < 4 ? 0 : 1;
                            int column = n < 4 ? n : n - 4;
                            Thumbnails.of(new ByteArrayInputStream(bytes))
                                    .sourceRegion(2 + column * 72, 37 + row * 73, 75, 73)
                                    .scale(1.0)
                                    .toFile("O:\\图片\\12306\\验证图片测试\\" + formatter.format(new Date()));
                            Thumbnails.of(new ByteArrayInputStream(Base64.getDecoder().decode(code)))
                                    .scale(1.0)
                                    .toFile("O:\\图片\\12306\\验证图片测试\\" + formatter.format(new Date()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        pageSb.append(n + 1).append(" ");
                        resultSb.append(",").append(answerList.get(n));
                        answerNum ++;
                        break;
                    }
                }
                if (answerNum == MAX_ANSWER_NUM) {
                    break;
                }
            }
        }
        String result = resultSb.replace(0, 1, "").toString();
        logger.info("验证结果：{}", pageSb.toString());
        return result;
    }

    private BufferedImage getAreaImageBuffer(byte[] bytes, int n) {
        InputStream stream = new ByteArrayInputStream(bytes);
        int row = n < 4 ? 0 : 1;
        int column = n < 4 ? n : n - 4;
        BufferedImage imageBuffered = null;
        try {
            imageBuffered = Thumbnails.of(stream)
                    .sourceRegion(2 + column * 72, 37 + row * 73, 75, 73)
                    .scale(1.0)
                    .asBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageBuffered;
    }

    private BufferedImage getOriginImageBuffer(String code) {
        InputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(code));
        BufferedImage originImageBuffer = null;
        try {
            originImageBuffer = Thumbnails.of(inputStream)
                    .scale(1.0)
                    .asBufferedImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return originImageBuffer;
    }
}
