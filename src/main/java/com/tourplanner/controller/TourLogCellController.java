package com.tourplanner.controller;

import com.tourplanner.TourPlannerApp;
import com.tourplanner.models.TourLog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class TourLogCellController {


    @FXML
    private Label logComment;

    private final VBox tourLogCell;


    public TourLogCellController() {
        FXMLLoader fxmlLoader = new FXMLLoader(TourPlannerApp.class.getResource("views/tour-log-cell.fxml"));
        fxmlLoader.setController(this);

        try {
            tourLogCell = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void updateData(TourLog tourLog) {
        //bind name property to name label
        logComment.textProperty().bind(tourLog.getCommentProperty());
    }

    public VBox getTourLogCell() {
        return tourLogCell;
    }
}
