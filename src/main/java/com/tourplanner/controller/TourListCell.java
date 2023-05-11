package com.tourplanner.controller;

import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public class TourListCell<Tour> extends ListCell<Tour> {

    private final TourListCellController tourListCellController = new TourListCellController();
    private final VBox tourListCell = tourListCellController.getTourListCell();

    public TourListCell() {
        super();
        this.indexProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.intValue() == 0){
                this.getStyleClass().add("first-list-cell");
            } else {
                this.getStyleClass().remove("first-list-cell");
            }
        });
    }

    @Override
    protected void updateItem(Tour tour, boolean empty) {
        super.updateItem(tour, empty);

        if (empty || tour == null) {
            setGraphic(null);
        } else {
            tourListCellController.updateData((com.tourplanner.models.Tour) tour);
            setGraphic(tourListCell);
        }
    }
}
