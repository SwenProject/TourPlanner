package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
import javafx.beans.property.*;
import javafx.scene.control.Label;

public class TourLogsController {

    private final TourLogic tourLogic; //shared TourLogic instance (contains selectedTour property)

    private final BooleanProperty editMode = new SimpleBooleanProperty(false); //for switching between view and edit mode


    //----TAB BAR FXML ELEMENTS----
    public Label tourName;

    public TourLogsController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize() {
        //bind listener to selectedTour object property
        //this calls loadTour() whenever the selectedTour is changed
        tourLogic.getSelectedTourProperty().addListener((observable, oldValue, newValue) -> loadTour(oldValue, newValue));

    }

    private void loadTour(Tour oldTour, Tour newTour){

        if(newTour == null) return; //if no tour is selected, do nothing

        //bind name to currently selected tour
        tourName.textProperty().bind(newTour.getNameProperty());

    }

    public void onSwitchTabs() {
        tourLogic.getCurrentTabProperty().set(0);
    }

}
