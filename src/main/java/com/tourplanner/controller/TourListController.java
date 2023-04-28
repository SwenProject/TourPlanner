package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
import javafx.event.ActionEvent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class TourListController {
    private final TourLogic tourLogic;
    public ListView<Tour> tourList;

    public TourListController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize(){
        tourList.setItems(tourLogic.getSearchedToursList());
        tourList.setCellFactory(new Callback<ListView<Tour>, ListCell<Tour>>() {
            @Override
            public TourListCell<Tour> call(ListView<Tour> listView) {
                return new TourListCell<>();
            }
        });

        //change selectedTour when new tour is selected in list
        tourList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                tourLogic.setSelectedTour(newValue);
            }
        });

        //change selected tour in list when selectedTour changes
        tourLogic.getSelectedTourProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != tourList.getSelectionModel().getSelectedItem()){
                tourList.getSelectionModel().select(newValue);
            }

        });

    }

    //temporary function for switching between tours for selectedTour since List is not implemented yet
    //TODO: remove
    public void onNextTour(ActionEvent actionEvent) {

        if(tourLogic.getAllToursList().isEmpty())
            return;

        if(tourLogic.getSelectedTourProperty().get() == null){
            tourLogic.setSelectedTour(tourLogic.getAllToursList().get(0));
            return;
        }

        //see which index tour is in tourlist
        int currentIndex = tourLogic.getAllToursList().indexOf(tourLogic.getSelectedTourProperty().get());

        //if it's the last tour in the list, set selectedTour to first tour in list
        if(currentIndex == tourLogic.getAllToursList().size()-1){
            tourLogic.setSelectedTour(tourLogic.getAllToursList().get(0));
        }

        //else set selectedTour to next tour in list
        else{
            tourLogic.setSelectedTour(tourLogic.getAllToursList().get(currentIndex+1));
        }

    }

    //temporary function for switching between tours for selectedTour since List is not implemented yet
    //TODO: remove
    public void onPreviousTour(ActionEvent actionEvent) {

        if(tourLogic.getAllToursList().isEmpty())
            return;

        if(tourLogic.getSelectedTourProperty().get() == null){
            tourLogic.setSelectedTour(tourLogic.getAllToursList().get(0));
            return;
        }

        //see which index tour is in tourlist
        int currentIndex = tourLogic.getAllToursList().indexOf(tourLogic.getSelectedTourProperty().get());

        //if it's the first tour in the list, set selectedTour to last tour in list
        if(currentIndex == 0){
            tourLogic.setSelectedTour(tourLogic.getAllToursList().get(tourLogic.getAllToursList().size()-1));
        }

        //else set selectedTour to previous tour in list
        else{
            tourLogic.setSelectedTour(tourLogic.getAllToursList().get(currentIndex-1));
        }
    }
}
