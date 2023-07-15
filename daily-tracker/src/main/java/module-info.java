module com.tracker {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.tracker to javafx.fxml;
    exports com.tracker;
}
