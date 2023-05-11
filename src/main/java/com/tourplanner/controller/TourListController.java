package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class TourListController {
    private final TourLogic tourLogic;
    public Label noToursLabel;
    public ListView<Tour> tourList;

    public TourListController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize(){

        //bind visibility of tourList to whether or not the list is empty
        tourList.visibleProperty().bind(tourLogic.getSearchedToursListProperty().emptyProperty().not());
        tourList.managedProperty().bind(tourLogic.getSearchedToursListProperty().emptyProperty().not());

        //bind visibility of noToursLabel to whether or not the list is empty
        noToursLabel.visibleProperty().bind(tourLogic.getSearchedToursListProperty().emptyProperty());
        noToursLabel.managedProperty().bind(tourLogic.getSearchedToursListProperty().emptyProperty());

        tourList.setItems(tourLogic.getSearchedToursList());
        tourList.setCellFactory(new Callback<ListView<Tour>, ListCell<Tour>>() {
            @Override
            public TourListCell<Tour> call(ListView<Tour> listView) {
                return new TourListCell<>();
            }
        });

        //change selectedTour when new tour is selected in list
        tourList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){ //user selected a new tour
                tourLogic.selectTour(newValue);
            }
        });

        //change selected tour in list when selectedTour changes
        tourLogic.getSelectedTourProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != tourList.getSelectionModel().getSelectedItem()){
                tourList.getSelectionModel().select(newValue);
            }

        });

    }

}
