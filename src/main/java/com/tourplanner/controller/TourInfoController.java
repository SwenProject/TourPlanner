package com.tourplanner.controller;

import com.tourplanner.enums.TransportType;
import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class TourInfoController {

    private final TourLogic tourLogic; //shared TourLogic instance (contains selectedTour property)

    private final BooleanProperty editMode = new SimpleBooleanProperty(false); //for switching between view and edit mode

    public StackPane tourInfos; //container for both view and edit mode (only hidden when no tour is selected)
    public Label noTourSelectedText; //placeholder text shown when no tour is selected
    public BorderPane viewModeBorderPane; //container for view mode
    public BorderPane editModeBorderPane; //container for edit mode


    //----VIEW MODE FXML ELEMENTS----
    public Label tourName;
    public Label startingPoint;
    public Label destinationPoint;
    public Label distance;
    public Label duration;
    public Label rating;
    public Label transportType;
    public TextArea description;

    //----EDIT MODE FXML ELEMENTS----
    public TextField tourNameEdit;
    public TextField startingPointEdit;
    public TextField destinationPointEdit;
    public TextField transportTypeEdit;
    public TextArea descriptionEdit;


    public TourInfoController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize(){

        //bind listener to selectedTour object property
        //this calls loadTour() whenever the selectedTour is changed
        tourLogic.getSelectedTourProperty().addListener((observable, oldValue, newValue) -> loadTour(oldValue, newValue));

        //bind visibility of viewModeBorderPane and editModeBorderPane to editMode
        //this automatically switches between view and edit mode when editMode is changed
        viewModeBorderPane.visibleProperty().bind(editMode.not());
        viewModeBorderPane.managedProperty().bind(editMode.not());
        editModeBorderPane.visibleProperty().bind(editMode);
        editModeBorderPane.managedProperty().bind(editMode);

        //bind all view mode elements to edit mode elements
        //this automatically copies the values from edit mode to view mode
        tourName.textProperty().bindBidirectional(tourNameEdit.textProperty());
        startingPoint.textProperty().bindBidirectional(startingPointEdit.textProperty());
        destinationPoint.textProperty().bindBidirectional(destinationPointEdit.textProperty());
        transportType.textProperty().bindBidirectional(transportTypeEdit.textProperty());
        description.textProperty().bindBidirectional(descriptionEdit.textProperty());
    }

    private void loadTour(Tour oldTour, Tour newTour){

        //if the old tour is not null, unbind all properties
        if(oldTour != null) {
            tourName.textProperty().unbindBidirectional(oldTour.getNameProperty());
            startingPoint.textProperty().unbindBidirectional(oldTour.getStartingPointProperty());
            destinationPoint.textProperty().unbindBidirectional(oldTour.getDestinationPointProperty());
            distance.textProperty().unbindBidirectional(oldTour.getDistanceProperty());
            duration.textProperty().unbindBidirectional(oldTour.getDurationProperty());
            //TODO: Handle Rating unbinding
            //TODO: Handle TransportType unbinding
            description.textProperty().unbindBidirectional(oldTour.getDescriptionProperty());
        }



        //if no tour was selected, hide tour info and show noTourSelectedText
        if(newTour == null) {
            noTourSelectedText.setVisible(true);
            noTourSelectedText.setManaged(true);
            tourInfos.setVisible(false);
            tourInfos.setManaged(false);
            return;
        }

        //if a tour was selected, show tour info and hide noTourSelectedText
        noTourSelectedText.setVisible(false);
        noTourSelectedText.setManaged(false);
        tourInfos.setVisible(true);
        tourInfos.setManaged(true);

        //if the selected tour is new, automatically switch to edit mode
        editMode.set(newTour.getId() == 0);

        //bind fields to currently selected tour
        tourName.textProperty().bindBidirectional(newTour.getNameProperty());
        startingPoint.textProperty().bindBidirectional(newTour.getStartingPointProperty());
        destinationPoint.textProperty().bindBidirectional(newTour.getDestinationPointProperty());
        description.textProperty().bindBidirectional(newTour.getDescriptionProperty());

        //distance needs a string converter because it is a double
        distance.textProperty().bind(Bindings.createStringBinding(() -> {
                    if (newTour.getDistanceProperty().get() == 0.0) {
                        return "Could not calculate distance";
                    } else {
                        return String.format("%.2f km", newTour.getDistanceProperty().get());
                    }
                }, newTour.getDistanceProperty()));

        //duration also needs a string converter because it is a Duration object
        duration.textProperty().bind(Bindings.createStringBinding(() -> {
                    if (newTour.getDurationProperty().get() == null) {
                        return "Could not calculate duration";
                    } else {
                        return String.format("%2d:%02d", newTour.getDurationProperty().get().getSeconds() / 3600, (newTour.getDurationProperty().get().getSeconds() % 3600) / 60);
                    }
                }, newTour.getDistanceProperty()));

        //TODO: Handle Rating binding
        //TODO: Handle TransportType binding
    }

    public void onEditTour(ActionEvent actionEvent) {
        editMode.set(true);
    }

    public void onSaveTour(ActionEvent actionEvent) {

        //save tour to database
        //this also recalculates the distance, duration, rating and map image
        tourLogic.saveSelectedTour();

        //disable edit mode
        editMode.set(false);
    }

    public void onCancelEdit(ActionEvent actionEvent) {

        //if tour was a new tour, cancelling deletes it
        //this sets the selected tour to null, which will hide the tour info
        if(tourLogic.getSelectedTourProperty().get().getId() == 0) {
            tourLogic.deleteSelectedTour();
            return;
        }

        //TODO: Handle cancelling an edit on an existing tour by reloading the tour from the database
    }

    public void onDeleteTour(ActionEvent actionEvent) {
        tourLogic.deleteSelectedTour();
    }
}
