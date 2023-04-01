package com.tourplanner.controller;

import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
public class MainController {

    public SplitPane mainContainer;
    public VBox leftContainer;

    public SplitPane rightContainer;

    public void initialize(){
        //maintains size of left container when resizing window
        SplitPane.setResizableWithParent(leftContainer, false);
    }

}