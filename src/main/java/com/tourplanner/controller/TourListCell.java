package com.tourplanner.controller;

import com.tourplanner.models.Tour;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;

public class TourListCell extends ListCell<Tour> {

    private final TourListCellController tourListCellController = new TourListCellController();
    private final HBox tourListCell = tourListCellController.getTourListCell();

    public TourListCell() {
        super();
        this.indexProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.intValue() == 0){
                this.getStyleClass().add("first-list-cell");
            } else {
                this.getStyleClass().remove("first-list-cell");
            }
        });

        setPrefWidth(0); //prevents horizontal scrollbar in listview
    }

    @Override
    protected void updateItem(Tour tour, boolean empty) {
        super.updateItem(tour, empty);

        if (empty || tour == null) {
            setGraphic(null);
        } else {
            tourListCellController.updateData(tour);
            setGraphic(tourListCell);
        }
    }
}
