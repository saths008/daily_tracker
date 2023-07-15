package com.tracker;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;

public class SpreadSheetController {

    public void readSpreadSheet(String filePath) {
        try {

            File file = new File(
                    filePath);
            System.out.println(file.getName());
            // Workbook workbook = WorkbookFactory.create(file);
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(0);
            Cell cell = row.getCell(0);
            System.out.println(cell.getStringCellValue());

            workbook.close();
        } catch (Exception e) {
            System.out.println("Error reading spreadsheet" + e.toString() + ": " +
                    e.getMessage());
        }
    }

    public void initialiseSpreadSheet(String filePath) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Daily Tracker");
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Date");
        try {
            File file = new File(
                    filePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            workbook.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

}
