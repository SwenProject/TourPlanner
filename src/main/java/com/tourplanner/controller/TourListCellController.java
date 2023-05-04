package com.tourplanner.controller;

import com.tourplanner.TourPlannerApp;
import com.tourplanner.models.Tour;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TourListCellController {


    @FXML
    private Label tourNameLabel;

    private final VBox tourListCell;


    public  TourListCellController() {
        FXMLLoader fxmlLoader = new FXMLLoader(TourPlannerApp.class.getResource("views/tour-list-cell.fxml"));
        fxmlLoader.setController(this);

        try {
            tourListCell = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void updateData(Tour tour) {
        //bind name property to name label
        tourNameLabel.textProperty().bind(tour.getNameProperty());
    }

    public VBox getTourListCell() {
        return tourListCell;
    }
}
