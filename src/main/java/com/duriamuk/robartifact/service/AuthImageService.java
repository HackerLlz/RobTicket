package com.duriamuk.robartifact.service;

import java.awt.image.BufferedImage;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-22 11:11
 */
public interface AuthImageService {
    void climbAuthFont(String message);

    void climbAuthImage(String message);

    Boolean similarImage(BufferedImage template, BufferedImage original);

    Boolean identifyAuthImage();

    void testIdentifyAuthImage(String message);

    Boolean isSameImage();

    String doOCR(byte[] bytes);
}
