package com.tourplanner.controller;

import com.tourplanner.TourPlannerApp;
import com.tourplanner.models.TourLog;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class TourLogCellController {

    private final VBox tourLogCell;

    @FXML
    public Label logDate;
    @FXML
    public Label logTime;
    @FXML
    public Label logComment;
    @FXML
    public Label logDifficulty;
    public IntegerProperty logRating = new SimpleIntegerProperty();
    @FXML
    public Region ratingStar1;
    @FXML
    public Region ratingStar2;
    @FXML
    public Region ratingStar3;
    @FXML
    public Region ratingStar4;
    @FXML
    public Region ratingStar5;
    @FXML
    public Label logTotalTime;
    @FXML
    public Button editButton;
    @FXML
    public Button deleteButton;

    //Functions for buttons
    Runnable startEditMode;
    Runnable deleteTourLog;


    public TourLogCellController(Runnable startEditMode, Runnable deleteTourLog) {

        this.startEditMode = startEditMode;
        this.deleteTourLog = deleteTourLog;

        FXMLLoader fxmlLoader = new FXMLLoader(TourPlannerApp.class.getResource("views/tour-log-cell.fxml"));
        fxmlLoader.setController(this);

        try {
            tourLogCell = fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unused")
    public void initialize(){ //is called automatically by FXMLLoader

        //bind onAction functions to buttons (can't be done in fxml here because controller is not set in fxml)
        editButton.setOnAction(event -> onEdit());
        deleteButton.setOnAction(event -> onDelete());

        //bind rating stars to rating property
        logRating.addListener((observable, oldValue, newValue) -> onRatingChanged(newValue.intValue()));

    }

    public void updateData(TourLog tourLog) {
        //bind name property to name label
        logDate.textProperty().bind(Bindings.createStringBinding(() -> {
            if (tourLog.getDateProperty().get() == null){ //if no date is set for log entry, something went wrong
                return "???";
            } else { //otherwise format date
                Date date = tourLog.getDateProperty().get();
                Calendar cal = new Calendar.Builder().setInstant(date).build();

                // format date object to dd.MM.yyyy
                String day = cal.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + cal.get(Calendar.DAY_OF_MONTH) : String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
                String month = (cal.get(Calendar.MONTH) + 1) < 10 ? "0" + (cal.get(Calendar.MONTH) + 1): (String.valueOf(cal.get(Calendar.MONTH)) + 1);
                String year = String.valueOf(cal.get(Calendar.YEAR));

                return day + "." + month + "." + year;
            }
        }, tourLog.getDateProperty()));
        logTime.textProperty().bind(Bindings.createStringBinding(() -> {
            if (tourLog.getDateProperty().get() == null){ //if no date is set for log entry, something went wrong
                return "???";
            } else { //otherwise format date
                Date date = tourLog.getDateProperty().get();
                Calendar cal = new Calendar.Builder().setInstant(date).build();

                // format date object to HH:mm in 24h format
                String hour = cal.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + cal.get(Calendar.HOUR_OF_DAY) : String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
                String minute = cal.get(Calendar.MINUTE) < 10 ? "0" + cal.get(Calendar.MINUTE) : String.valueOf(cal.get(Calendar.MINUTE));

                return hour + ":" + minute;
            }
        }, tourLog.getDateProperty()));
        logComment.textProperty().bind(tourLog.getCommentProperty());
        logDifficulty.textProperty().bind(Bindings.createStringBinding(() -> {
            if (tourLog.getDifficultyProperty().get() == null){ //if no difficulty is set for log entry, something went wrong
                return "???";
            } else { //otherwise format difficulty
                return switch (tourLog.getDifficultyProperty().get()) {
                    case EASY -> "Easy";
                    case MEDIUM -> "Medium";
                    case HARD -> "Hard";
                    case EXPERT -> "Expert";
                    default -> "???";
                };
            }
        }, tourLog.getDifficultyProperty()));
        logRating.bind(tourLog.getRatingProperty());
        logTotalTime.textProperty().bind(Bindings.createStringBinding(() -> {
            if (tourLog.getTotalTimeProperty().get() == null){ //if no total time is set for log entry, something went wrong
                return "???";
            } else { //otherwise format total time
                long totalTime = tourLog.getTotalTimeProperty().get().getSeconds();
                long hours = totalTime / 3600;
                long minutes = (totalTime % 3600) / 60;

                return hours + "h " + minutes + "min";
            }
        }, tourLog.getTotalTimeProperty()));

    }

    public VBox getTourLogCell() {
        return tourLogCell;
    }

    public void onEdit(){
        startEditMode.run();
    }

    public void onDelete(){
        deleteTourLog.run();
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
