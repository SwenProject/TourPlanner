package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;

public class MapController {
    private final TourLogic tourLogic;

    public MapController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize(){

        //tourLogic.getSelectedTourProperty().addListener((observable, oldValue, newValue) -> loadTourImage(newValue));
    }
    //TODO
    // loadTourImage() -> databinden auf image in map.fxml
    // Listener adden in initialize function so wie in TourInfoController -> loadTourImage()
}
