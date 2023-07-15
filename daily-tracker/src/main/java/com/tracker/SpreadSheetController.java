package com.tracker;

import org.apache.poi.ss.usermodel.*;

import java.io.File;

public class SpreadSheetController {

    public void readSpreadSheet() {
        try {
            Workbook workbook = WorkbookFactory.create(new File("daily-tracker/src/main/resources/test.ods"));
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(0);
            Cell cell = row.getCell(0);
            System.out.println(cell.getStringCellValue());
        } catch (Exception e) {
            System.out.println("Error reading spreadsheet");
        }
    }

}
