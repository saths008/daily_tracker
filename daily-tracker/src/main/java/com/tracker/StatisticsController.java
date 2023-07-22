package com.tracker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

public class StatisticsController {
    private VBox lineChartContainer;

    public StatisticsController(VBox lineChartContainer) {
        this.lineChartContainer = lineChartContainer;
    }

    public List<Double> getHeaderValues(String filePath, String headerName) {
        List<Double> headerValues = new ArrayList<>();
        try {
            File file = new File(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            Row row = sheet.getRow(0);
            for (Cell cell : row) {
                if (cell != null && cell.getStringCellValue().equals(headerName)) {
                    int columnIndex = cell.getColumnIndex();
                    for (int i = 1; i <= lastRowNum; i++) {
                        row = sheet.getRow(i);
                        cell = row.getCell(columnIndex);
                        if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                            headerValues.add(cell.getNumericCellValue());
                        }
                    }
                }
            }
            workbook.close();
        } catch (Exception e) {
            System.out.println("getHeaderValues Error: " + e.toString());
        }
        return headerValues;
    }

    public List<String> getAllDates(String filePath) {
        List<String> dates = new ArrayList<String>();
        try {
            File file = new File(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                Cell cell = row.getCell(0);
                if (cell != null) {
                    String value = cell.getStringCellValue().strip();
                    if (!value.isEmpty()) {
                        dates.add(value);
                    }
                }
            }
            workbook.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return dates;
    }

    public void createLineChart(String filePath, String header) {

        List<Double> rowData = this.getHeaderValues(filePath, header);
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        List<String> dates = formatDates(getAllDates(filePath));

        System.out.println("rowData.size(): " + rowData.size());
        System.out.println("dates.size(): " + dates.size());
        System.out.println("rowData: " + rowData);
        System.out.println("dates: " + dates);
        ObservableList<String> categories = FXCollections.observableArrayList(dates);
        xAxis.setCategories(categories);
        xAxis.setAutoRanging(true);
        yAxis.setAutoRanging(true);
        // Create a new LineChart
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(header);

        // Create a data series for the row
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(header);

        // Add data points to the series
        for (int i = 0; i < rowData.size(); i++) {
            series.getData().add(new XYChart.Data<>(dates.get(i), rowData.get(i)));
        }
        // Add the series to the line chart
        lineChart.getData().add(series);

        // Add the line chart to the container
        lineChartContainer.getChildren().add(lineChart);

    }

    public List<String> formatDates(List<String> dates) {
        String regexPattern = "^(\\S+ \\S+ \\d{1,2}) .* (\\d{4})$";
        Pattern pattern = Pattern.compile(regexPattern);
        for (int i = 0; i < dates.size(); i++) {
            String date = dates.get(i);
            System.out.println(date);
            Matcher matcher = pattern.matcher(date);
            String formattedDate = "";
            if (matcher.find()) {
                formattedDate = matcher.group(1);
                String year = matcher.group(2);
                formattedDate = formattedDate + " " + year;
                dates.set(i, formattedDate); // Update the value in the dates list
            } else {
                System.out.println("No match found.");
            }
        }
        return dates;
    }

}
