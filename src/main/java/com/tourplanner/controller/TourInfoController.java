package com.tourplanner.controller;

import com.tourplanner.enums.TransportType;
import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
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
        tourLogic.getSelectedTourProperty().addListener((observable, oldValue, newValue) -> loadTour(newValue));

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

    private void loadTour(Tour tour){

        //if no tour was selected, hide tour info and show noTourSelectedText
        if(tour == null) {
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
        editMode.set(tour.getId() == null);

        //load tour info into view mode elements
        //this automatically also copies the values to edit mode elements because of the bidirectional binding
        tourName.setText(tour.getName());
        startingPoint.setText(tour.getStartingPoint());
        destinationPoint.setText(tour.getDestinationPoint());
        distance.setText(String.valueOf(tour.getDistance()));
        duration.setText(String.valueOf(tour.getDuration()));
        rating.setText(String.valueOf(tour.getRating()));
        transportType.setText((String.valueOf(tour.getTransportType().ordinal())));
        description.setText(tour.getDescription());
    }

    public void onEditTour(ActionEvent actionEvent) {
        editMode.set(true);
    }

    public void onSaveTour(ActionEvent actionEvent) {

        //save changes to tour object
        tourLogic.getSelectedTourProperty().get().setName(tourNameEdit.getText());
        tourLogic.getSelectedTourProperty().get().setStartingPoint(startingPointEdit.getText());
        tourLogic.getSelectedTourProperty().get().setDestinationPoint(destinationPointEdit.getText());
        tourLogic.getSelectedTourProperty().get().setTransportType(TransportType.values()[Integer.parseInt(transportTypeEdit.getText())]);
        tourLogic.getSelectedTourProperty().get().setDescription(descriptionEdit.getText());

        //save tour to database
        //this also recalculates the distance, duration, rating and map image
        tourLogic.saveSelectedTour();

        //disable edit mode
        editMode.set(false);
    }

    public void onCancelEdit(ActionEvent actionEvent) {

        //if tour was a new tour, cancelling deletes it
        //this sets the selected tour to null, which will hide the tour info
        if(tourLogic.getSelectedTourProperty().get().getId() == null) {
            tourLogic.deleteSelectedTour();
            return;
        }

        //otherwise reload tour to load old values (changes were not yet saved to tour object or db)
        //reloading the tour also switches to view mode
        loadTour(tourLogic.getSelectedTourProperty().get());
    }

    public void onDeleteTour(ActionEvent actionEvent) {
        tourLogic.deleteSelectedTour();
    }
}
