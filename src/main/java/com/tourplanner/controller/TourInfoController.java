package com.tourplanner.controller;

import com.tourplanner.enums.TransportType;
import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

public class TourInfoController {

    private final TourLogic tourLogic; //shared TourLogic instance (contains selectedTour property)

    private final BooleanProperty editMode = new SimpleBooleanProperty(false); //for switching between view and edit mode

    public StackPane tourInfos; //container for both view and edit mode (only hidden when no tour is selected)
    public VBox noTourSelectedContainer; //placeholder text shown when no tour is selected
    public BorderPane viewModeBorderPane; //container for view mode
    public BorderPane editModeBorderPane; //container for edit mode


    //----VIEW MODE FXML ELEMENTS----
    public Label tourName;
    public Label startingPoint;
    public Label destinationPoint;
    public Label distance;
    public Label duration;
    public HBox distanceIcon;
    public HBox distanceSpinner;
    public HBox durationIcon;
    public HBox durationSpinner;
    public TextArea description;
    public VBox noDescriptionContainer;
    public Region transportTypeCarIcon;
    public Region transportTypeFeetIcon;
    public Region transportTypeBikeIcon;
    public Region ratingStar1;
    public Region ratingStar2;
    public Region ratingStar3;
    public Region ratingStar4;
    public Region ratingStar5;

    //----EDIT MODE FXML ELEMENTS----
    public TextField tourNameEdit;
    public TextField startingPointEdit;
    public TextField destinationPointEdit;
    public TextArea descriptionEdit;
    public Label transportTypeSelectorCar;
    public Label transportTypeSelectorFeet;
    public Label transportTypeSelectorBike;

    //----PROPERTIES FOR BINDING----
    private final ObjectProperty<TransportType> currentTransportType = new SimpleObjectProperty<>();
    private final IntegerProperty currentRating = new SimpleIntegerProperty();
    private final BooleanProperty distanceIsLoading = new SimpleBooleanProperty(false);
    private final BooleanProperty durationIsLoading = new SimpleBooleanProperty(false);


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
        description.textProperty().bindBidirectional(descriptionEdit.textProperty());

        //bind transportType Icons to currentTransportType property
        //this automatically switches the icons when currentTransportType is changed
        transportTypeCarIcon.visibleProperty().bind(currentTransportType.isEqualTo(TransportType.CAR));
        transportTypeCarIcon.managedProperty().bind(currentTransportType.isEqualTo(TransportType.CAR));
        transportTypeFeetIcon.visibleProperty().bind(currentTransportType.isEqualTo(TransportType.FEET));
        transportTypeFeetIcon.managedProperty().bind(currentTransportType.isEqualTo(TransportType.FEET));
        transportTypeBikeIcon.visibleProperty().bind(currentTransportType.isEqualTo(TransportType.BIKE));
        transportTypeBikeIcon.managedProperty().bind(currentTransportType.isEqualTo(TransportType.BIKE));

        //bind distance loading spinner to distanceIsLoading property
        distanceSpinner.visibleProperty().bind(distanceIsLoading);
        distanceSpinner.managedProperty().bind(distanceIsLoading);
        distanceIcon.visibleProperty().bind(distanceIsLoading.not());
        distanceIcon.managedProperty().bind(distanceIsLoading.not());

        //bind duration loading spinner to durationIsLoading property
        durationSpinner.visibleProperty().bind(durationIsLoading);
        durationSpinner.managedProperty().bind(durationIsLoading);
        durationIcon.visibleProperty().bind(durationIsLoading.not());
        durationIcon.managedProperty().bind(durationIsLoading.not());

        //add listener to currentTransportType property to change the transportType selector in edit mode
        currentTransportType.addListener((observable, oldValue, newValue) -> onTransportTypeChanged(newValue));

        //add listener to currentRating property to change the rating stars in view mode
        currentRating.addListener((observable, oldValue, newValue) -> onRatingChanged(newValue.intValue()));

        //bind no description container to description text
        noDescriptionContainer.visibleProperty().bind(description.textProperty().isEmpty());
        noDescriptionContainer.managedProperty().bind(description.textProperty().isEmpty());

    }

    public void switchTabs(MouseEvent mouseEvent) {
        tourLogic.getCurrentTabProperty().set(1);
    }

    private void loadTour(Tour oldTour, Tour newTour){

        //if the old tour is not null, unbind all old properties
        if(oldTour != null) {
            tourName.textProperty().unbindBidirectional(oldTour.getNameProperty());
            startingPoint.textProperty().unbindBidirectional(oldTour.getStartingPointProperty());
            destinationPoint.textProperty().unbindBidirectional(oldTour.getDestinationPointProperty());
            distance.textProperty().unbindBidirectional(oldTour.getDistanceProperty());
            duration.textProperty().unbindBidirectional(oldTour.getDurationProperty());
            currentTransportType.unbindBidirectional(oldTour.getTransportTypeProperty());
            currentRating.unbindBidirectional(oldTour.getRatingProperty());
            description.textProperty().unbindBidirectional(oldTour.getDescriptionProperty());
        }

        //if no tour was selected, hide tour info and show noTourSelectedText
        if(newTour == null) {
            noTourSelectedContainer.setVisible(true);
            noTourSelectedContainer.setManaged(true);
            tourInfos.setVisible(false);
            tourInfos.setManaged(false);
            return;
        }

        //if a tour was selected, show tour info and hide noTourSelectedText
        noTourSelectedContainer.setVisible(false);
        noTourSelectedContainer.setManaged(false);
        tourInfos.setVisible(true);
        tourInfos.setManaged(true);

        //if the selected tour is new, automatically switch to edit mode
        editMode.set(newTour.getId() == 0);

        //bind fields to currently selected tour
        tourName.textProperty().bindBidirectional(newTour.getNameProperty());
        startingPoint.textProperty().bindBidirectional(newTour.getStartingPointProperty());
        destinationPoint.textProperty().bindBidirectional(newTour.getDestinationPointProperty());
        currentTransportType.bindBidirectional(newTour.getTransportTypeProperty());
        currentRating.bindBidirectional(newTour.getRatingProperty());
        description.textProperty().bindBidirectional(newTour.getDescriptionProperty());

        //distance needs a string converter because it is a double
        distance.textProperty().bind(Bindings.createStringBinding(() -> {
                    if (newTour.getDistanceProperty().get() == 0.0) {
                        distanceIsLoading.set(false);
                        return "Error";
                    } else if (newTour.getDistanceProperty().get() == -1.0){
                        distanceIsLoading.set(true);
                        return "...";
                    } else {
                        distanceIsLoading.set(false);
                        return String.format("%.2f km", newTour.getDistanceProperty().get());
                    }
                }, newTour.getDistanceProperty()));

        //duration also needs a string converter because it is a Duration object
        duration.textProperty().bind(Bindings.createStringBinding(() -> {
                    if (newTour.getDurationProperty().get() == null) {
                        durationIsLoading.set(false);
                        return "Error";
                    } else if (newTour.getDurationProperty().get().getSeconds() == -1){
                        durationIsLoading.set(true);
                        return "...";
                    } else {
                        durationIsLoading.set(false);
                        return String.format("%d:%02d", newTour.getDurationProperty().get().getSeconds() / 3600, (newTour.getDurationProperty().get().getSeconds() % 3600) / 60);
                    }
                }, newTour.getDurationProperty()));
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

        //reload tour from database to get old values
        tourLogic.reloadSelectedTourFromDB();

        //disable edit mode
        editMode.set(false);
    }

    private void onTransportTypeChanged(TransportType newTransportType){
        transportTypeSelectorCar.getStyleClass().clear();
        transportTypeSelectorFeet.getStyleClass().clear();
        transportTypeSelectorBike.getStyleClass().clear();

        switch (newTransportType) {
            case CAR -> transportTypeSelectorCar.getStyleClass().add("selectedTransportType");
            case FEET -> transportTypeSelectorFeet.getStyleClass().add("selectedTransportType");
            case BIKE -> transportTypeSelectorBike.getStyleClass().add("selectedTransportType");
        }
    }

    public void changeTransportTypeToCar(MouseEvent mouseEvent) {
        this.currentTransportType.set(TransportType.CAR);
    }

    public void changeTransportTypeToFeet(MouseEvent mouseEvent) {
        this.currentTransportType.set(TransportType.FEET);
    }

    public void changeTransportTypeToBike(MouseEvent mouseEvent) {
        this.currentTransportType.set(TransportType.BIKE);
    }

    public void onDeleteTour(ActionEvent actionEvent) {
        tourLogic.deleteSelectedTour();
    }

    private void onRatingChanged(int newRating) {
        ratingStar1.getStyleClass().remove("active-rating-star");
        ratingStar2.getStyleClass().remove("active-rating-star");
        ratingStar3.getStyleClass().remove("active-rating-star");
        ratingStar4.getStyleClass().remove("active-rating-star");
        ratingStar5.getStyleClass().remove("active-rating-star");

        if(newRating >= 1) ratingStar1.getStyleClass().add("active-rating-star");
        if(newRating >= 2) ratingStar2.getStyleClass().add("active-rating-star");
        if(newRating >= 3) ratingStar3.getStyleClass().add("active-rating-star");
        if(newRating >= 4) ratingStar4.getStyleClass().add("active-rating-star");
        if(newRating >= 5) ratingStar5.getStyleClass().add("active-rating-star");

    }
}
