package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;

public class MainController {
    private final TourLogic tourLogic;
    public SplitPane mainContainer;

    public VBox leftContainer;
    public SplitPane rightContainer;


    public MainController(TourLogic tourLogic){
        this.tourLogic = tourLogic;
    }

    public void initialize(){
        //maintains size of left container when resizing window
        SplitPane.setResizableWithParent(leftContainer, false);
    }

}