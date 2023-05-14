package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;

public class MainController {
    private final TourLogic tourLogic;
    public SplitPane mainContainer;

    public VBox leftContainer;
    public SplitPane rightContainer;


    // tour logs and tour info files for switching between tabs
    public VBox tourInfoFile;
    public VBox tourLogsFile;

    public MainController(TourLogic tourLogic){
        this.tourLogic = tourLogic;
    }

    public void initialize(){
        //maintains size of left container when resizing window
        SplitPane.setResizableWithParent(leftContainer, false);

        //bind visibility of tourInfoFile to currentTabProperty
        tourInfoFile.visibleProperty().bind(tourLogic.getCurrentTabProperty().isEqualTo(0));
        tourInfoFile.managedProperty().bind(tourLogic.getCurrentTabProperty().isEqualTo(0));

        //bind visibility of tourLogsFile to currentTabProperty
        tourLogsFile.visibleProperty().bind(tourLogic.getCurrentTabProperty().isEqualTo(1));
        tourLogsFile.managedProperty().bind(tourLogic.getCurrentTabProperty().isEqualTo(1));
    }

}