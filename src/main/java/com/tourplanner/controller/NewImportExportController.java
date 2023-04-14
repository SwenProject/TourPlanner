package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;
import javafx.event.ActionEvent;

public class NewImportExportController {

    private final TourLogic tourLogic;

    public NewImportExportController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize(){
    }


    public void onCreateNewTrip(ActionEvent actionEvent) {
        tourLogic.createNewTour();
    }
}