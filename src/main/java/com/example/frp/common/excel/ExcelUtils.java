package com.example.frp.common.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-06 18:38
 */
public class ExcelUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
    private static SimpleDateFormat formatter = new SimpleDateFormat("_yyyy-MM-dd_HH时mm分ss秒");
    private static Workbook workbook;

    public static void exportExcel(String path, String tableName, List<ExcelCellEntity> cellEntityList, HttpServletResponse response) {
        workbook = readExcel(path);
        buildCellData(workbook, cellEntityList);
        writeExcel(response, workbook, tableName);
    }

    private static Workbook readExcel(String filePath) {
        InputStream in = null;
        Workbook workbook = null;
        try {
            in = ExcelUtils.class.getClassLoader().getResourceAsStream(buildPath(filePath));
            if (ObjectUtils.isEmpty(in)) {
                throw new FileNotFoundException();
            }
            workbook = new HSSFWorkbook(in);
        } catch (FileNotFoundException e) {
            logger.info("文件路径错误");
            e.printStackTrace();
        } catch (IOException e) {
            logger.info("文件输入流错误");
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                logger.error("输入流关闭失败");
                e.printStackTrace();
            }
        }
        return workbook;
    }

    private static void writeExcel(HttpServletResponse response, Workbook workbook, String fileName) {
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            response.setContentType("application/ms-excel;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename="
                    .concat(String.valueOf(URLEncoder.encode(buildFileName(fileName), "UTF-8"))));
            workbook.write(out);
        } catch (IOException e) {
            logger.info("输入流错误");
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                logger.error("输出流关闭失败");
                e.printStackTrace();
            }
        }
    }

    private static void buildCellData(Workbook workbook, List<ExcelCellEntity> cellEntityList) {
        int curSheet = 0;
        int curRow = 0;
        Sheet sheet = workbook.getSheetAt(0);
        Row row = sheet.getRow(0);
        for (ExcelCellEntity cellEntity : cellEntityList) {
            if (curSheet != cellEntity.getSheet()) {
                curSheet = cellEntity.getSheet();
                sheet = workbook.getSheetAt(cellEntity.getSheet());  // 获取sheet
            }
            if (curRow != cellEntity.getRow()) {
                curRow = cellEntity.getRow();
                row = sheet.getRow(cellEntity.getRow());  //获取行
            }
            setCellContent(row, cellEntity.getCol(), cellEntity.getValue());  // 设置该列的值
        }
    }

    private static <T>void setCellContent(Row row, Integer key, T value) {
        if (ObjectUtils.isEmpty(row)) {
            return;
        }
        if (value instanceof String) {
            row.getCell(key).setCellValue((String) value);
        }
        if (value instanceof Double) {
            row.getCell(key).setCellValue((Double) value);
        }
        if (value instanceof Date) {
            row.getCell(key).setCellValue((Date) value);
        }
        if (value instanceof Calendar) {
            row.getCell(key).setCellValue((Calendar) value);
        }
        if (value instanceof RichTextString) {
            row.getCell(key).setCellValue((RichTextString) value);
        }
    }

    private static String buildFileName(String name) {
        String dateStr = formatter.format(Calendar.getInstance().getTime());
        String fileName = name + dateStr + ".xls";
        return fileName;
    }

    private static String buildPath(String path) {
        String prePath = path.startsWith("/") ? "static" : "static/";
        return prePath + path;
    }
}
