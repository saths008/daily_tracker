package com.tracker;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SpreadSheetController implements Initializable {

    @FXML
    private TextField filePathTextField;

    @FXML
    private Label headersFoundLabel;

    @FXML
    private TextField valuesTextField;

    @FXML
    private Label updateDailyMessageLabel;

    @FXML
    private Label initialiseSpreadSheetMessageLabel;

    // Statistics
    @FXML
    private VBox lineChartContainer;

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

    public List<String> getAllDates() {
        List<String> dates = new ArrayList<String>();
        String filePath = filePathTextField.getText();
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

    public List<Double> getHeaderValues(String headerName) {
        List<Double> headerValues = new ArrayList<>();
        String filePath = filePathTextField.getText();
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
                        if (cell !=  null && cell.getCellType() == CellType.NUMERIC) {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        filePathTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                String[] headers = readSpreadSheetHeaders().split(",");
                lineChartContainer.getChildren().clear(); // Clear existing line charts

                // Create a line chart for each row header
                for (String header : headers) {
                    createLineChart(header);
                }
            }
        });
    }

    private void createLineChart(String header) {

        List<Double> rowData = getHeaderValues(header);
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        List<String> dates = formatDates(getAllDates());

        System.out.println("rowData.size(): " + rowData.size());
        System.out.println("dates.size(): " + dates.size());
        System.out.println("rowData: " + rowData);
        System.out.println("dates: " + dates);
        ObservableList<String> categories = FXCollections.observableArrayList(dates);
        xAxis.setCategories(categories);
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

    @FXML
    public void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        Stage stage = (Stage) filePathTextField.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            filePathTextField.setText(selectedFile.getAbsolutePath());
            headersFoundLabel.setText(readSpreadSheetHeaders());
        }
    }

    @FXML
    private void goHome(ActionEvent event) throws IOException {
        App.setRoot("main");
    }

    public void readSpreadSheet() {
        try {
            String filePath = filePathTextField.getText();
            File file = new File(
                    filePath);
            System.out.println(file.getName());
            // Workbook workbook = WorkbookFactory.create(file);
            Workbook workbook = WorkbookFactory.create(file);
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(0);
            Cell cell = row.getCell(0);
            // System.out.println(cell.getStringCellValue());

            workbook.close();
        } catch (Exception e) {
            System.out.println("Error reading spreadsheet" + e.toString() + ": " +
                    e.getMessage());
        }
    }

    @FXML
    public void initialiseSpreadSheet() {
        String filePath = filePathTextField.getText();
        String[] headers = headersFoundLabel.getText().split(",");
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
            initialiseSpreadSheetMessageLabel.setText("Spreadsheet initialisation successful");
        } catch (Exception e) {
            System.out.println(e.toString());
            initialiseSpreadSheetMessageLabel.setText("Spreadsheet initialisation failed");
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
     * @return String containing the headers of the spreadsheet
     */
    public String readSpreadSheetHeaders() {
        String filePath = filePathTextField.getText();
        String headers = "";
        try {
            File file = new File(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(0);
            for (Cell cell : row) {
                if (cell.getStringCellValue() != null && !cell.getStringCellValue().equals("Date")) {
                    headers = headers + cell.getStringCellValue() + ",";
                }
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
                Cell cell = row.createCell(i + 1);
                if (isNumeric(values[i])) {
                    double numericValue = Double.parseDouble(values[i]);
                    cell.setCellValue(numericValue);
                } else {
                    cell.setCellValue(values[i]);
                }
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

    private boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Wrapper around addDailyUpdateController to add a daily update with the
     * current date
     * 
     * @param filePath Path to the spreadsheet
     * @param values   Array of values to be added to the spreadsheet
     */
    @FXML
    public void addDailyUpdateDefault() {
        String filePath = filePathTextField.getText();
        String[] values = valuesTextField.getText().split(",");
        Date date = new Date();
        boolean success = addDailyUpdateController(filePath, date.toString(), values);
        if (success) {
            updateDailyMessageLabel.setText("Daily update added successfully");
        } else {
            updateDailyMessageLabel.setText("Error adding daily update");
        }
    }

}
