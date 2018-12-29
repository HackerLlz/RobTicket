package com.example.frp.common.excel;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-07 16:00
 */
public class ExcelCellEntity {
    Integer sheet;
    Integer row;
    Integer col;
    Object value;

    public Integer getSheet() {
        return sheet;
    }

    public void setSheet(Integer sheet) {
        this.sheet = sheet;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getCol() {
        return col;
    }

    public void setCol(Integer col) {
        this.col = col;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


    public ExcelCellEntity(Integer row, Integer col, Object value) {
        this.sheet = 0;
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public ExcelCellEntity(Integer sheet, Integer row, Integer col, Object value) {
        this.sheet = sheet;
        this.row = row;
        this.col = col;
        this.value = value;
    }

    public ExcelCellEntity() {
    }

    @Override
    public String toString() {
        return "ExcelCellEntity{" +
                "sheet=" + sheet +
                ", row=" + row +
                ", col=" + col +
                ", value=" + value +
                '}';
    }
}
