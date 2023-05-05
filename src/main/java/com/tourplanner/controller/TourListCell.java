package com.tourplanner.controller;

import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public class TourListCell<Tour> extends ListCell<Tour> {

    private final TourListCellController tourListCellController = new TourListCellController();
    private final VBox tourListCell = tourListCellController.getTourListCell();

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
