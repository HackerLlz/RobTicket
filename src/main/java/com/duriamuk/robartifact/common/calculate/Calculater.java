package com.duriamuk.robartifact.common.calculate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2019-01-12 20:30
 */
public class Calculater {
    private static final Logger logger = LoggerFactory.getLogger(Calculater.class);
    private static final List<Integer> list = new ArrayList<>(8);
    private static final int MAX = 6;

    public static void buildPositionList() {
        StringBuffer sb = new StringBuffer();
        sb.append("\"");
        for (int n = 0; n < MAX; n++) {
            build(n, n, 0, list, sb);
        }
        logger.warn("positionList: {}", sb.toString());
    }

    private static void build(int n, int curN, int i, List<Integer> list, StringBuffer sb) {
        if (curN >= 0) {
            for (int j = i; j < MAX - curN; j++) {
                if (curN == 0) {
                    for (int index = 0; index < n + 1; index++) {
                        if (index != n) {
                            sb.append(list.get(index)).append(",");
                        } else {
                            sb.append(j).append("\"");
                        }
                    }
                    if (j != MAX - curN - 1) {
                        sb.append(",").append("\"");
                    }
                } else {
                    list.add(n - curN, j);
                    if (curN == 1) {
                        sb.append(",").append("\"");
                    }
                    build(n, curN - 1, j + 1, list, sb);
                }
            }
        }
    }
}
