package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TourDetailsController {
    private final TourLogic tourLogic;

    public VBox noTourSelectedContainer; //shown when no tour is selected
    public StackPane tourDetails; //shown when a tour is selected, contains both tourInfos and tourLogs


    // tour logs and tour info files for switching between tabs
    public StackPane tourInfoFile;
    public StackPane tourLogsFile;

    public TourDetailsController(TourLogic tourLogic){
        this.tourLogic = tourLogic;
    }

    public void initialize(){

        //bind visibility of tourInfos and noTourSelectedContainer to selectedTourProperty
        //this automatically hides the tourInfos when no tour is selected
        tourDetails.visibleProperty().bind(tourLogic.getSelectedTourProperty().isNotNull());
        tourDetails.managedProperty().bind(tourLogic.getSelectedTourProperty().isNotNull());
        noTourSelectedContainer.visibleProperty().bind(tourLogic.getSelectedTourProperty().isNull());
        noTourSelectedContainer.managedProperty().bind(tourLogic.getSelectedTourProperty().isNull());

        //bind visibility of tourInfoFile to currentTabProperty
        tourInfoFile.visibleProperty().bind(tourLogic.getCurrentTabProperty().isEqualTo(0));
        tourInfoFile.managedProperty().bind(tourLogic.getCurrentTabProperty().isEqualTo(0));

        //bind visibility of tourLogsFile to currentTabProperty
        tourLogsFile.visibleProperty().bind(tourLogic.getCurrentTabProperty().isEqualTo(1));
        tourLogsFile.managedProperty().bind(tourLogic.getCurrentTabProperty().isEqualTo(1));
    }

}