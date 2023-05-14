package com.tourplanner.controller;

import com.tourplanner.models.TourLog;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public class TourLogCell extends ListCell<TourLog> {

    private final TourLogCellController tourLogCellController = new TourLogCellController();
    private final VBox tourLogCell = tourLogCellController.getTourLogCell();

    public TourLogCell() {
        super();
        this.indexProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.intValue() == 0){
                this.getStyleClass().add("first-log-cell");
            } else {
                this.getStyleClass().remove("first-log-cell");
            }
        });
    }

    @Override
    protected void updateItem(TourLog tourLog, boolean empty) {
        super.updateItem(tourLog, empty);

        if (empty || tourLog == null) {
            setGraphic(null);
        } else {
            tourLogCellController.updateData(tourLog);
            setGraphic(tourLogCell);
        }
    }
}
