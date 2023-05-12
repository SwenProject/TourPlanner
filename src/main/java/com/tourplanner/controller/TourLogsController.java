package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;
import javafx.beans.property.*;
import javafx.scene.input.MouseEvent;

public class TourLogsController {

    private final TourLogic tourLogic; //shared TourLogic instance (contains selectedTour property)

    private final BooleanProperty editMode = new SimpleBooleanProperty(false); //for switching between view and edit mode

    public TourLogsController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize() {


    }

    public void onSwitchTabs(MouseEvent actionEvent) {
        tourLogic.getCurrentTabProperty().set(0);
    }

}
