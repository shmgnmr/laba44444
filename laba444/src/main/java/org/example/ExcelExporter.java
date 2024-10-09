package org.example;

import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ExcelExporter {
    public static void exportToExcel(LinkedHashMap<String, LinkedHashMap<String, Double>> data, String sheetName, Workbook workbook) {
        try {
            Sheet sheet = workbook.createSheet(sheetName);

            Row headerRow = sheet.createRow(0);
            Cell headerCell0 = headerRow.createCell(0);
            headerCell0.setCellValue("Ключ");
            Cell headerCell1 = headerRow.createCell(1);
            headerCell1.setCellValue("Год");
            Cell headerCell2 = headerRow.createCell(2);
            headerCell2.setCellValue("Потребление");

            int rowNum = 1;
            for (String key : data.keySet()) {
                HashMap<String, Double> yearConsumption = data.get(key);
                for (String year : yearConsumption.keySet()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(key);
                    row.createCell(1).setCellValue(year);
                    row.createCell(2).setCellValue(yearConsumption.get(year));
                }
            }
        } catch (IllegalArgumentException e){
            System.out.println("Такой расчет уже был произведен");
        }
    }

}
