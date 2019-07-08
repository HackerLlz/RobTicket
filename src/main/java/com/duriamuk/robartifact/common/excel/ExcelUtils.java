package com.duriamuk.robartifact.common.excel;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    /**
     * 读取resources/static中的模板excel, 写入数据后输出到response
     * @param path
     * @param tableName
     * @param cellEntityList
     */
    public static void exportExcel(String path, String tableName, List<ExcelCellEntity> cellEntityList) {
        workbook = readExcel(buildStaticPath(path));
        buildCellData(workbook, cellEntityList);
        writeExcelOnResponse(workbook, tableName);
    }

    /**
     * 读取绝对路径下的excel
     * @param filePath
     * @return
     */
    public static Workbook readExcel(String filePath) {
        InputStream in = null;
        Workbook workbook = null;
        try {
            in = new FileInputStream(filePath);
            if (ObjectUtils.isEmpty(in)) {
                throw new FileNotFoundException();
            }
            workbook = new XSSFWorkbook(in);
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

    /**
     * 写入到绝对路径下的excel
     * @param workbook
     * @param path
     */
    public static void writeExcel(Workbook workbook, String path) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
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

    /**
     * 构建单元格
     * @param workbook
     * @param cellEntityList
     */
    public static void buildCellData(Workbook workbook, List<ExcelCellEntity> cellEntityList) {
        for (ExcelCellEntity cellEntity : cellEntityList) {
            Sheet sheet = workbook.getSheetAt(cellEntity.getSheet());
            Row row = sheet.getRow(cellEntity.getRow());
            if (ObjectUtils.isEmpty(row)) {
                row = sheet.createRow(cellEntity.getRow());
            }
            Cell cell = row.getCell(cellEntity.getCol());
            if (ObjectUtils.isEmpty(cell)) {
                cell = row.createCell(cellEntity.getCol());
            }
            // 设置该列的值
            setCellContent(cell, cellEntity.getValue());
        }
    }

    /**
     * 获取指定sheet中, 指定行开始的列
     * @param workbook
     * @param cellEntityList
     * @return
     */
    public static List<String> listCol(Workbook workbook, ExcelCellEntity cellEntityList){
        List<String> list = new ArrayList<>();
        Sheet sheet = workbook.getSheetAt(cellEntityList.getSheet());
        if (sheet == null || cellEntityList.getRow() > sheet.getLastRowNum()) {
            return list;
        }
        //遍历该sheet指定列的行
        for (int rowNum = cellEntityList.getRow(); rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            Cell cell = row.getCell(cellEntityList.getCol());
            if (ObjectUtils.isEmpty(cell)) {
                return list;
            }
            cell.setCellType(Cell.CELL_TYPE_STRING);
            list.add(cell.getStringCellValue());
        }
        return list;
    }


    private static void writeExcelOnResponse(Workbook workbook, String fileName) {
        HttpServletResponse response = getResponse();
        if (!ObjectUtils.isEmpty(response)) {
            logger.error("无法获取response");
            return;
        }
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

    private static <T> void setCellContent(Cell cell, T value) {
        if (ObjectUtils.isEmpty(cell)) {
            logger.error("单元格为空");
            return;
        }
        if (value instanceof String) {
            cell.setCellValue((String) value);
        }
        if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }
        if (value instanceof Date) {
            cell.setCellValue((Date) value);
        }
        if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        }
        if (value instanceof RichTextString) {
            cell.setCellValue((RichTextString) value);
        }
    }

    private static String buildFileName(String name) {
        String dateStr = formatter.format(Calendar.getInstance().getTime());
        String fileName = name + dateStr + ".xls";
        return fileName;
    }

    private static String buildStaticPath(String path) {
        String prePath = path.startsWith("/") ? "static" : "static/";
        return prePath + path;
    }

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (!ObjectUtils.isEmpty(servletRequestAttributes)) {
            HttpServletResponse response = servletRequestAttributes.getResponse();
            return response;
        }
        return null;
    }
}
