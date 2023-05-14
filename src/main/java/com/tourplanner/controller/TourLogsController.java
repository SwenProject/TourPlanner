package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
import com.tourplanner.models.TourLog;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

import java.util.List;

public class TourLogsController {

    private final TourLogic tourLogic; //shared TourLogic instance (contains selectedTour property)

    private final BooleanProperty editMode = new SimpleBooleanProperty(false); //for switching between view and edit mode


    //----TAB BAR FXML ELEMENTS----
    public Label tourName;

    //----TOUR LOGS LIST----
    public ListView<TourLog> tourLogsList;

    //----PROPERTIES FOR BINDING----
    private final ObjectProperty<List<TourLog>> currentTourLogs = new SimpleObjectProperty<>();
    private ObservableList<TourLog> currentTourLogsObservableList;

    public TourLogsController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize() {
        //bind listener to selectedTour object property
        //this calls loadTour() whenever the selectedTour is changed
        tourLogic.getSelectedTourProperty().addListener((observable, oldValue, newValue) -> loadTour(oldValue, newValue));

        //bind listener to currentTourLogs object property to rebuild observable list when currentTourLogList changes
        currentTourLogs.addListener((observable, oldValue, newValue) -> loadNewTourLogsList(newValue));

        tourLogsList.setCellFactory(new Callback<ListView<TourLog>, ListCell<TourLog>>() {
            @Override
            public TourLogCell call(ListView<TourLog> tourLogListView) {
                return new TourLogCell();
            }
        });

    }

    private void loadTour(Tour oldTour, Tour newTour){

        if(oldTour != null){
            //unbind tour logs list from old tour
            currentTourLogs.unbindBidirectional(oldTour.getTourLogsProperty());
        }

        if(newTour == null) return; //if no tour is selected, do nothing

        //bind name to currently selected tour
        tourName.textProperty().bind(newTour.getNameProperty());

        //bind tour logs list to currently selected tour
        currentTourLogs.bindBidirectional(newTour.getTourLogsProperty());

    }

    private void loadNewTourLogsList(List<TourLog> newTourLogsList){
        if(newTourLogsList == null) {
            currentTourLogsObservableList = null;
            tourLogsList.setItems(null);
            return;
        }

        currentTourLogsObservableList = FXCollections.observableList(newTourLogsList);
        tourLogsList.setItems(currentTourLogsObservableList);
    }

    public void onSwitchTabs() {
        tourLogic.getCurrentTabProperty().set(0);
    }

}
