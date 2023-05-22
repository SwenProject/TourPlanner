package com.tourplanner.controller;

import com.tourplanner.TourPlannerApp;
import com.tourplanner.enums.TransportType;
import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.time.Duration;
import java.util.Objects;

public class TourInfoController {

    private final TourLogic tourLogic; //shared TourLogic instance (contains selectedTour property)
    private final BooleanProperty editMode = new SimpleBooleanProperty(false); //for switching between view and edit mode

    public BorderPane viewModeBorderPane; //container for view mode
    public BorderPane editModeBorderPane; //container for edit mode

    //----TAB BAR FXML ELEMENTS----
    public Label tourName;


    //----VIEW MODE FXML ELEMENTS----
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
    public Button editTourButton;

    //----EDIT MODE FXML ELEMENTS----
    public TextField tourNameEdit;
    public TextField startingPointEdit;
    public TextField destinationPointEdit;
    public TextArea descriptionEdit;
    public Label transportTypeSelectorCar;
    public Label transportTypeSelectorFeet;
    public Label transportTypeSelectorBike;

    //----FIELDS FOR SAVING PREVIOUS VALUES FOR EDIT MODE CANCEL----
    private String previousTourName;
    private String previousStartingPoint;
    private String previousDestinationPoint;
    private String previousDescription;
    private TransportType previousTransportType;

    //----PROPERTIES FOR BINDING----
    private final ObjectProperty<TransportType> currentTransportType = new SimpleObjectProperty<>();
    private final IntegerProperty currentRating = new SimpleIntegerProperty();
    private final BooleanProperty distanceIsLoading = new SimpleBooleanProperty(false);
    private final BooleanProperty durationIsLoading = new SimpleBooleanProperty(false);
    private final BooleanProperty imageIsLoading = new SimpleBooleanProperty(false);


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

        //bind editTourButton to durationIsLoading and imageIsLoading property (disables button while route calculation is in progress)
        editTourButton.disableProperty().bind(imageIsLoading.or(durationIsLoading));
    }

    public void switchTabs() {
        tourLogic.getCurrentTabProperty().set(1);
    }

    private void loadTour(Tour oldTour, Tour newTour){

        if(newTour != null && newTour.isNew()){
            editMode.set(true); // if the new tour is a new tour, switch to edit mode
        } else if (oldTour != null && oldTour.isNew()){
            editMode.set(false); //if the old tour was a new tour, switch back to view mode
        } else if (editMode.get()){
            onCancelEdit(); //if we are still in edit mode when new tour is loaded, cancel edit
        }

        //if the old tour is not null, unbind all old properties
        //we don't need to unbind the unidirectional bindings, because they are automatically unbound when a new property is bound
        if(oldTour != null) {
            tourName.textProperty().unbindBidirectional(oldTour.getNameProperty());
            startingPoint.textProperty().unbindBidirectional(oldTour.getStartingPointProperty());
            destinationPoint.textProperty().unbindBidirectional(oldTour.getDestinationPointProperty());
            currentTransportType.unbindBidirectional(oldTour.getTransportTypeProperty());
            currentRating.unbindBidirectional(oldTour.getRatingProperty());
            description.textProperty().unbindBidirectional(oldTour.getDescriptionProperty());
        }

        //if no tour was selected, hide tour info and show noTourSelectedText
        if(newTour == null) return; //if no new tour was selected, we are done

        //bind fields to currently selected tour
        tourName.textProperty().bindBidirectional(newTour.getNameProperty());
        startingPoint.textProperty().bindBidirectional(newTour.getStartingPointProperty());
        destinationPoint.textProperty().bindBidirectional(newTour.getDestinationPointProperty());
        currentTransportType.bindBidirectional(newTour.getTransportTypeProperty());
        currentRating.bindBidirectional(newTour.getRatingProperty());
        description.textProperty().bindBidirectional(newTour.getDescriptionProperty());

        //distance needs a string converter because it is a double
        distance.textProperty().bind(Bindings.createStringBinding(() -> {
                    if (newTour.getDistanceProperty().get() == 0.0 || newTour.getDistanceProperty().get() == -1.0){ //-1 is loading, 0 is for new tours
                        return "...";
                    } else if (newTour.getDistanceProperty().get() == -2.0) { //-2 is error
                        return "Error";
                    } else { //otherwise show distance
                        return String.format("%.2f km", newTour.getDistanceProperty().get());
                    }
                }, newTour.getDistanceProperty()));

        //duration also needs a string converter because it is a Duration object
        duration.textProperty().bind(Bindings.createStringBinding(() -> {
                    if (newTour.getDurationProperty().get() == null || newTour.getDurationProperty().get().getSeconds() == -1){ //-1 is loading, null is for new tours
                        return "...";
                    } else if (newTour.getDurationProperty().get().getSeconds() == -2) { //-2 is error
                        return "Error";
                    } else {
                        return String.format("%d:%02d", newTour.getDurationProperty().get().getSeconds() / 3600, (newTour.getDurationProperty().get().getSeconds() % 3600) / 60);
                    }
                }, newTour.getDurationProperty()));

        distanceIsLoading.bind(newTour.getDistanceProperty().isEqualTo(-1));
        durationIsLoading.bind(newTour.getDurationProperty().isEqualTo(Duration.ofSeconds(-1)));
        imageIsLoading.bind(newTour.getPathToMapImageProperty().isEqualTo("loading"));
    }

    public void onEditTour() {

        previousTourName = tourName.getText();
        previousStartingPoint = startingPoint.getText();
        previousDestinationPoint = destinationPoint.getText();
        previousDescription = description.getText();
        previousTransportType = currentTransportType.get();

        editMode.set(true);
    }

    public void onSaveTour() {

        //if tour was a new tour, always save it
        if(tourLogic.getSelectedTourProperty().get().isNew()) {
            tourLogic.saveSelectedTour();
            editMode.set(false);
            return;
        }

        //else check if any of the fields relevant for the route calculation have changed
        //this includes startingPoint, destinationPoint and transportType
        if(!tourLogic.getSelectedTourProperty().get().getStartingPointProperty().get().equals(previousStartingPoint) ||
                !tourLogic.getSelectedTourProperty().get().getDestinationPointProperty().get().equals(previousDestinationPoint) ||
                tourLogic.getSelectedTourProperty().get().getTransportTypeProperty().get() != previousTransportType) {

            //if they have changed, use saveSelectedTour to recalculate the route
            tourLogic.saveSelectedTour();
        } else {
            //if they haven't changed, just update the tour
            tourLogic.updateSelectedTourWithoutRecalculating();
        }

        //disable edit mode
        editMode.set(false);
    }

    public void onCancelEdit() {

        //disable edit mode
        editMode.set(false);

        //if tour was a new tour, cancelling deletes it
        //this sets the selected tour to null, which will hide the tour info
        if(tourLogic.getSelectedTourProperty().get().isNew()) {
            tourLogic.deleteSelectedTour();
            return;
        }

        //reset fields to their previous values
        tourName.textProperty().set(previousTourName);
        startingPoint.textProperty().set(previousStartingPoint);
        destinationPoint.textProperty().set(previousDestinationPoint);
        description.textProperty().set(previousDescription);
        currentTransportType.set(previousTransportType);
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

    public void changeTransportTypeToCar() {
        this.currentTransportType.set(TransportType.CAR);
    }

    public void changeTransportTypeToFeet() {
        this.currentTransportType.set(TransportType.FEET);
    }

    public void changeTransportTypeToBike() {
        this.currentTransportType.set(TransportType.BIKE);
    }

    public void onDeleteTour() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delete Tour");
        alert.setHeaderText("Are you sure you want to delete this tour?");
        alert.initStyle(StageStyle.TRANSPARENT);

        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType buttonTypeDelete = new ButtonType("Yes", ButtonBar.ButtonData.YES);

        alert.getButtonTypes().setAll(buttonTypeDelete, buttonTypeCancel);

        //add css file to alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(TourPlannerApp.class.getResource("css/confirm-tour-delete.css")).toExternalForm());
        dialogPane.getStyleClass().add("deleteAlertBox");

        //replace alert image with custom image
        Image image = new Image(Objects.requireNonNull(TourPlannerApp.class.getResourceAsStream("images/delete_tour.png")));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(70);
        alert.setGraphic(imageView);

        //set styleClass of deleteButton so that it can be styled differently than the cancel button
        Button yesButton = (Button) alert.getDialogPane().lookupButton(buttonTypeDelete);
        yesButton.getStyleClass().add("deleteButton");

        //set background of scene to transparent so that the alert box can have rounded corners
        Scene scene = dialogPane.getScene();
        scene.setFill(Color.TRANSPARENT);

        //ensure that the alert box always appears in front of the main window on the same screen
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(viewModeBorderPane.getScene().getWindow());

        // show alert box and wait for response
        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeDelete) {
                tourLogic.deleteSelectedTour();
            } else if (response == buttonTypeCancel) {
                //do nothing
                //maybe log that the user cancelled deleting the tour
            }
        });

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
