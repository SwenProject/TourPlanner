package com.tourplanner.controller;

import com.tourplanner.models.TourLog;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class TourLogCell extends ListCell<TourLog> {

    private final TourLogCellController tourLogCellController;
    private final VBox tourLogCell;

    //functions for buttons
    Consumer<TourLog> startEditMode;
    Consumer<TourLog> deleteTourLog;

    public TourLogCell(Consumer<TourLog> startEditMode, Consumer<TourLog> deleteTourLog) {
        super();

        this.startEditMode = startEditMode;
        this.deleteTourLog = deleteTourLog;

        Runnable startEditModeForThisTour = this::startEditMode;
        Runnable deleteTourLogForThisTour = this::deleteTourLog;

        this.tourLogCellController = new TourLogCellController(startEditModeForThisTour, deleteTourLogForThisTour);
        this.tourLogCell = tourLogCellController.getTourLogCell();
        this.indexProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue.intValue() == 0){
                this.getStyleClass().add("first-log-cell");
            } else {
                this.getStyleClass().remove("first-log-cell");
            }
        });

        setPrefWidth(0); //prevents horizontal scrollbar in listview
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

    public void startEditMode(){
        startEditMode.accept(getItem());
    }

    public void deleteTourLog(){
        deleteTourLog.accept(getItem());
    }
}
