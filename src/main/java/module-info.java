module TourPlanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.persistence;
    requires lombok;
    requires java.sql;
    requires org.hibernate.orm.core;


    opens com.tourplanner to javafx.fxml;
    exports com.tourplanner;
    exports com.tourplanner.controller;
    opens com.tourplanner.controller to javafx.fxml;
    opens com.tourplanner.models to org.hibernate.orm.core;
}