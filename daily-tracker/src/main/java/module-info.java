module com.tracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens com.tracker to javafx.fxml;

    exports com.tracker;
}
