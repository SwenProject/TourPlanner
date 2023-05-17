package com.tourplanner.controller;

import com.tourplanner.enums.Difficulty;
import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
import com.tourplanner.models.TourLog;
import javafx.beans.property.*;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.time.Duration;
import java.util.Date;
import java.util.function.Consumer;

public class TourLogsController {

    private final TourLogic tourLogic; //shared TourLogic instance (contains selectedTour property)

    private final BooleanProperty editMode = new SimpleBooleanProperty(false); //for switching between view and edit mode


    //----TAB BAR FXML ELEMENTS----
    public Label tourName;

    //----FXML ELEMENTS----
    public VBox noLogsContainer;
    public ListView<TourLog> tourLogsList;
    public VBox viewModeContainer;

    //----EDIT MODE----
    public VBox editModeContainer;
    public TextField logCommentEdit;
    public TextField logDifficultyEdit;
    public TextField logRatingEdit;
    public TextField logTotalTimeEdit;
    public final ObjectProperty<TourLog> tourLogInEdit = new SimpleObjectProperty<>();


    //----PROPERTIES FOR BINDING----
    private final ListProperty<TourLog> currentTourLogs = new SimpleListProperty<>();
    public TourLogsController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize() {
        //bind listener to selectedTour object property
        //this calls loadTour() whenever the selectedTour is changed
        tourLogic.getSelectedTourProperty().addListener((observable, oldValue, newValue) -> loadTour(oldValue, newValue));

        Consumer<TourLog> startEditMode = this::startEditMode;
        Consumer<TourLog> deleteTourLog = this::deleteTourLog;

        tourLogsList.setCellFactory(tourLogListView -> new TourLogCell(startEditMode, deleteTourLog));

        //bind listener to currentTourLogs list property
        currentTourLogs.addListener((observable, oldValue, newValue) -> {
            //set the new list as the list for the tour logs list view
            tourLogsList.setItems(newValue);
        });

        noLogsContainer.visibleProperty().bind(currentTourLogs.emptyProperty());
        noLogsContainer.managedProperty().bind(currentTourLogs.emptyProperty());
        tourLogsList.visibleProperty().bind(currentTourLogs.emptyProperty().not());
        tourLogsList.managedProperty().bind(currentTourLogs.emptyProperty().not());

        viewModeContainer.visibleProperty().bind(editMode.not());
        viewModeContainer.managedProperty().bind(editMode.not());
        editModeContainer.visibleProperty().bind(editMode);
        editModeContainer.managedProperty().bind(editMode);

        //if edit mode is disabled, reset the tour log in edit
        editMode.addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                tourLogInEdit.set(null);
            }
        });
    }

    private void loadTour(Tour oldTour, Tour newTour){

        editMode.set(false);

        if(oldTour != null) {
            //unbind old tour logs list
            currentTourLogs.unbindBidirectional(oldTour.getTourLogsProperty());
        }

        if(newTour == null) return; //if no tour is selected, do nothing

        currentTourLogs.bindBidirectional(newTour.getTourLogsProperty());

        //bind name to currently selected tour
        tourName.textProperty().bind(newTour.getNameProperty());

    }

    public void onSwitchTabs() {
        tourLogic.getCurrentTabProperty().set(0);
    }

    public void deleteTourLog(TourLog tourLog) {
        System.out.println("delete tour log " + tourLog.getComment());
        currentTourLogs.remove(tourLog);
        tourLogic.updateSelectedTourWithoutRecalculating();
    }

    public void startEditMode(TourLog tourLog) {
        System.out.println("start edit mode on tour log " + tourLog.getComment());
        tourLogInEdit.set(tourLog);
        logCommentEdit.setText(tourLog.getComment());
        logDifficultyEdit.setText(String.valueOf(tourLog.getDifficulty().ordinal()));
        logRatingEdit.setText(String.valueOf(tourLog.getRating()));
        logTotalTimeEdit.setText(String.valueOf(tourLog.getTotalTime()));
        editMode.set(true);
    }

    public void onCancelEdit() {
        editMode.set(false);
    }

    public void onSaveEdit() {

        if(tourLogInEdit.get() == null) { //tour log is new
            TourLog tourLog = new TourLog();
            tourLog.setComment(logCommentEdit.getText());
            tourLog.setDifficulty(Difficulty.values()[Integer.parseInt(logDifficultyEdit.getText())]);
            tourLog.setRating(Integer.parseInt(logRatingEdit.getText()));
            tourLog.setTotalTime(Duration.parse(logTotalTimeEdit.getText()));
            tourLog.setDate(new Date());
            currentTourLogs.add(tourLog);
            tourLogic.updateSelectedTourWithoutRecalculating();
            editMode.set(false);
            return;
        }

        TourLog tourLog = tourLogInEdit.get();
        tourLog.setComment(logCommentEdit.getText());
        tourLog.setDifficulty(Difficulty.values()[Integer.parseInt(logDifficultyEdit.getText())]);
        tourLog.setRating(Integer.parseInt(logRatingEdit.getText()));
        tourLog.setTotalTime(Duration.parse(logTotalTimeEdit.getText()));
        editMode.set(false);
        tourLogic.updateSelectedTourWithoutRecalculating();
    }

    public void onAddLog() {
        System.out.println("created new tour log");
        tourLogInEdit.set(null);
        logCommentEdit.setText("");
        logDifficultyEdit.setText("0");
        logRatingEdit.setText("0");
        logTotalTimeEdit.setText("PT10M0S");
        editMode.set(true);
    }
}
