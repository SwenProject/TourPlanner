package com.tourplanner.controller;

import com.tourplanner.TourPlannerApp;
import com.tourplanner.enums.TransportType;
import com.tourplanner.models.Tour;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

import java.io.IOException;

public class TourListCellController {

    private final HBox tourListCell;

    @FXML
    public Label tourNameLabel;
    @FXML
    public Label tourFromLabel;
    @FXML
    public Label tourToLabel;
    @FXML
    public Region transportTypeCarIcon;
    @FXML
    public Region transportTypeFeetIcon;
    @FXML
    public Region transportTypeBikeIcon;
    @FXML
    public Label tourDistanceLabel;
    @FXML
    public Label tourRatingLabel;

    private final ObjectProperty<TransportType> currentTransportType = new SimpleObjectProperty<>();

    public  TourListCellController() {
        FXMLLoader fxmlLoader = new FXMLLoader(TourPlannerApp.class.getResource("views/tour-list-cell.fxml"));
        fxmlLoader.setController(this);

        try {
            tourListCell = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @SuppressWarnings("unused")
    public void initialize(){ //is called automatically by FXMLLoader

        //bind transportType Icons to currentTransportType property
        //this automatically switches the icons when currentTransportType is changed
        transportTypeCarIcon.visibleProperty().bind(currentTransportType.isEqualTo(TransportType.CAR));
        transportTypeCarIcon.managedProperty().bind(currentTransportType.isEqualTo(TransportType.CAR));
        transportTypeFeetIcon.visibleProperty().bind(currentTransportType.isEqualTo(TransportType.FEET));
        transportTypeFeetIcon.managedProperty().bind(currentTransportType.isEqualTo(TransportType.FEET));
        transportTypeBikeIcon.visibleProperty().bind(currentTransportType.isEqualTo(TransportType.BIKE));
        transportTypeBikeIcon.managedProperty().bind(currentTransportType.isEqualTo(TransportType.BIKE));

    }

    public void updateData(Tour tour) {
        //bind properties to tour properties
        tourNameLabel.textProperty().bind(tour.getNameProperty());
        tourFromLabel.textProperty().bind(tour.getStartingPointProperty());
        tourToLabel.textProperty().bind(tour.getDestinationPointProperty());
        tourRatingLabel.textProperty().bind(tour.getRatingProperty().asString());
        currentTransportType.bind(tour.getTransportTypeProperty());
        tourDistanceLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            if (tour.getDistanceProperty().get() == 0.0 || tour.getDistanceProperty().get() == -1.0){ //-1 is loading, 0 is for new tours
                return "calculating...";
            } else if (tour.getDistanceProperty().get() == -2.0) { //-2 is error
                return "-";
            } else { //otherwise show distance
                return String.format("~%.0fkm", tour.getDistanceProperty().get());
            }
        }, tour.getDistanceProperty()));
    }

    public HBox getTourListCell() {
        return tourListCell;
    }
}
