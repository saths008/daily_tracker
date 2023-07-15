package com.tracker;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

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

    public void initialiseSpreadSheet(String filePath, String[] headers) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Daily Tracker");
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Date");
        int numberOfHeaders = headers.length;
        for (int i = 0; i < numberOfHeaders; i++) {
            cell = row.createCell(i + 1);
            cell.setCellValue(headers[i]);
        }
        try {
            File file = new File(
                    filePath);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                workbook.close();
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        }
    }

    /**
     * 
     * @param filePath Path to the spreadsheet
     * @return ArrayList of headers
     */
    public ArrayList<String> readSpreadSheetHeaders(String filePath) {
        ArrayList<String> headers = new ArrayList<>();
        try {
            File file = new File(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(0);
            for (Cell cell : row) {
                headers.add(cell.getStringCellValue());
            }
            System.out.println(headers);
            workbook.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
        return headers;
    }

    /**
     * 
     * @param filePath Path to the spreadsheet
     * @param date     Date of the daily update
     * @param values   Array of values to be added to the spreadsheet
     * @return boolean on whether the operation was successful
     */
    public boolean addDailyUpdateController(String filePath, String date, String[] values) {
        try {
            File file = new File(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(filePath));
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            Row row = sheet.createRow(lastRowNum + 1);
            row.createCell(0).setCellValue(date);
            int numberOfValues = values.length;

            for (int i = 0; i < numberOfValues; i++) {
                row.createCell(i + 1).setCellValue(values[i]);
            }

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            fileOutputStream.close();
            workbook.close();
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        }
    }

    /**
     * Wrapper around addDailyUpdateController to add a daily update with the
     * current date
     * 
     * @param filePath Path to the spreadsheet
     * @param values   Array of values to be added to the spreadsheet
     * @return boolean on whether the operation was successful
     */
    public boolean addDailyUpdateDefault(String filePath, String[] values) {
        Date date = new Date();
        return addDailyUpdateController(filePath, date.toString(), values);
    }

}
