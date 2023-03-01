module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.tourplanner to javafx.fxml;
    exports com.tourplanner;
}