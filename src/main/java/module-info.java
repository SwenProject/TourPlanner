module TourPlanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires jakarta.persistence;
    requires lombok;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires org.json;
    requires kernel;
    requires io;
    requires layout;
    requires com.fasterxml.jackson.databind;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;

    opens com.tourplanner to javafx.fxml;
    exports com.tourplanner;
    exports com.tourplanner.controller;
    exports com.tourplanner.models;
    exports com.tourplanner.services;
    exports com.tourplanner.repositories;
    exports com.tourplanner.enums;
    opens com.tourplanner.controller to javafx.fxml;
    opens com.tourplanner.models to org.hibernate.orm.core;
    exports com.tourplanner.logic;
    opens com.tourplanner.logic to javafx.fxml;
    opens com.tourplanner.services to javafx.fxml;
    exports com.tourplanner.services.interfaces;
    opens com.tourplanner.services.interfaces to javafx.fxml;
}