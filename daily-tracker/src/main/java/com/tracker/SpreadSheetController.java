package com.tracker;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SpreadSheetController implements Initializable {

    @FXML
    private TextField filePathTextField;

    @FXML
    private TextField headersTextField;

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

    private ArrayList<String> presetHeaders = new ArrayList<String>() {
        {
            add("Date");
            add("Notes");
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location.getPath().endsWith("stats.fxml")) {
            StatisticsController statisticsController = new StatisticsController(lineChartContainer);
            filePathTextField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.isEmpty()) {
                    String[] headers = readSpreadSheetHeaders().split(",");

                    lineChartContainer.getChildren().clear(); // Clear existing line charts

                    // Create a line chart for each row header
                    for (String header : headers) {
                        if (!presetHeaders.contains(header)) {
                            statisticsController.createLineChart(filePathTextField.getText(), header);
                        }

                    }
                }
            });
        }
    }

    @FXML
    /**
     * Opens a file chooser to select a spreadsheet
     */
    public void selectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select File");
        Stage stage = (Stage) filePathTextField.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            filePathTextField.setText(selectedFile.getAbsolutePath());
        }
        if (headersFoundLabel != null) {
            headersFoundLabel.setText(readSpreadSheetHeaders());
        }

    }

    @FXML

    /**
     * Redirects to the main page
     * 
     * @param event
     * @throws IOException if the main.fxml file is not found
     */
    private void goHome(ActionEvent event) throws IOException {
        App.setRoot("main");
    }

    @FXML
    /**
     * Initialises the spreadsheet with the headers found in the headersFoundLabel
     */
    public void initialiseSpreadSheet() {
        String filePath = filePathTextField.getText();
        String[] headers = headersTextField.getText().split(",");
        XSSFWorkbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Daily Tracker");
        Row row = sheet.createRow(0);
        for (int i = 0; i < this.presetHeaders.size(); i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(presetHeaders.get(i));
        }

        int numberOfPresetHeaders = presetHeaders.size();
        int numberOfHeaders = headers.length;
        Cell cell;
        for (int i = 0; i < numberOfHeaders; i++) {
            cell = row.createCell(i + numberOfPresetHeaders);
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
                if (cell.getStringCellValue() != null) {
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

    private boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 
     * @param filePath Path to the spreadsheet
     * @param date     Date of the daily update
     * @param values   Array of values to be added to the spreadsheet
     * @return boolean on whether the operation was successful
     */
    public boolean addDailyUpdateController(String filePath, String date, String[] values, String notes) {
        try {
            File file = new File(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(filePath));
            Sheet sheet = workbook.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            Row row = sheet.createRow(lastRowNum + 1);
            // create a cell for each of the preset headers
            row.createCell(0).setCellValue(date);
            row.createCell(1).setCellValue(notes);
            int numberOfValues = values.length;

            // starts at index 1 of the values array because index 0 is the notes
            for (int i = 1; i < numberOfValues; i++) {
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
        String notes = values[0];
        Date date = new Date();
        boolean success = addDailyUpdateController(filePath, date.toString(), values, notes);
        if (success) {
            updateDailyMessageLabel.setText("Daily update added successfully");
        } else {
            updateDailyMessageLabel.setText("Error adding daily update");
        }
    }

}
