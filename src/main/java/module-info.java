module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.tourplanner to javafx.fxml;
    exports com.tourplanner;
    exports com.tourplanner.controller;
    opens com.tourplanner.controller to javafx.fxml;
}