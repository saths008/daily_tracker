package com.tracker;

import java.io.IOException;
import javafx.fxml.FXML;

public class MainScreenController {

    @FXML
    private void goToDailyUpdate() throws IOException {
        App.setRoot("dailyUpdate");
    }

    @FXML
    private void goToInitialiseSpreadSheet() throws IOException {
        App.setRoot("initialiseSpreadSheet");
    }

    @FXML
    private void goToStats() throws IOException {
        App.setRoot("stats");
    }
}