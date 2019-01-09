package com.duriamuk.robartifact.controller;

import com.duriamuk.robartifact.common.excel.ExcelCellEntity;
import com.duriamuk.robartifact.common.excel.ExcelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-06 18:32
 */
@Controller
@RequestMapping("/excel")
public class ExcelController {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    @RequestMapping(value = "export", method = RequestMethod.GET)
    public String export() {
        return "excel/index";
    }

    @RequestMapping(value = "exportExcel", method = RequestMethod.GET)
    public void exportExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = "excel/aaa.xls";
        String tableName = "aaa表";
        List<ExcelCellEntity> list = new ArrayList<>();
        list.add(new ExcelCellEntity(0, 0, "aaa"));
        ExcelUtils.exportExcel(path, tableName, list, response);
        logger.info("excel导出完成");
    }
}
