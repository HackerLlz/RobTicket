package com.duriamuk.robartifact.common.excel;

import lombok.Data;

/**
 * @author: DuriaMuk
 * @description:
 * @create: 2018-12-07 16:00
 */
@Data
public class ExcelCellEntity {
    Integer sheet;
    Integer row;
    Integer col;
    Object value;

    public ExcelCellEntity(Integer row, Integer col) {
        this.sheet = 0;
        this.row = row;
        this.col = col;
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
