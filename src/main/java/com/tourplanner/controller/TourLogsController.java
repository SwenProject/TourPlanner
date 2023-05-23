package com.tourplanner.controller;

import com.tourplanner.TourPlannerApp;
import com.tourplanner.enums.Difficulty;
import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
import com.tourplanner.models.TourLog;
import javafx.beans.property.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

public class TourLogsController {

    private final TourLogic tourLogic; //shared TourLogic instance (contains selectedTour property)

    private final BooleanProperty editMode = new SimpleBooleanProperty(false); //for switching between view and edit mode


    //----TAB BAR FXML ELEMENTS----
    public Label tourName;

    //----FXML ELEMENTS----
    public VBox noLogsContainer;
    public ListView<TourLog> tourLogsList;
    public VBox viewModeContainer;

    //----EDIT MODE----
    public VBox editModeContainer;
    public TextArea logCommentEdit;
    public ObjectProperty<Difficulty> logDifficultyEdit = new SimpleObjectProperty<>();
    public Label difficultySelectorEasy;
    public Label difficultySelectorMedium;
    public Label difficultySelectorHard;
    public Label difficultySelectorExpert;
    public IntegerProperty logRatingEdit = new SimpleIntegerProperty();
    public Region ratingStar1;
    public Region ratingStar2;
    public Region ratingStar3;
    public Region ratingStar4;
    public Region ratingStar5;
    public TextField logTotalTimeEditHours;
    public TextField logTotalTimeEditMinutes;
    public final ObjectProperty<TourLog> tourLogInEdit = new SimpleObjectProperty<>();
    public Button editModeSaveButton;


    //----PROPERTIES FOR BINDING----
    private final ListProperty<TourLog> currentTourLogs = new SimpleListProperty<>();


    public TourLogsController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize() {
        //bind listener to selectedTour object property
        //this calls loadTour() whenever the selectedTour is changed
        tourLogic.getSelectedTourProperty().addListener((observable, oldValue, newValue) -> loadTour(oldValue, newValue));

        Consumer<TourLog> startEditMode = this::startEditMode;
        Consumer<TourLog> deleteTourLog = this::deleteTourLog;

        tourLogsList.setCellFactory(tourLogListView -> new TourLogCell(startEditMode, deleteTourLog));

        //bind listener to currentTourLogs list property
        currentTourLogs.addListener((observable, oldValue, newValue) -> {
            //set the new list as the list for the tour logs list view
            tourLogsList.setItems(newValue);
        });

        noLogsContainer.visibleProperty().bind(currentTourLogs.emptyProperty());
        noLogsContainer.managedProperty().bind(currentTourLogs.emptyProperty());
        tourLogsList.visibleProperty().bind(currentTourLogs.emptyProperty().not());
        tourLogsList.managedProperty().bind(currentTourLogs.emptyProperty().not());

        viewModeContainer.visibleProperty().bind(editMode.not());
        viewModeContainer.managedProperty().bind(editMode.not());
        editModeContainer.visibleProperty().bind(editMode);
        editModeContainer.managedProperty().bind(editMode);

        //if edit mode is disabled, reset the tour log in edit
        editMode.addListener((observable, oldValue, newValue) -> {
            if(!newValue) {
                tourLogInEdit.set(null);
            }
        });

        //bind edit difficulty to logDifficultyEdit property
        logDifficultyEdit.addListener((observable, oldValue, newValue) -> onEditDifficultyChanged(newValue));

        //bind edit rating stars to logRatingEdit property
        logRatingEdit.addListener((observable, oldValue, newValue) -> onEditRatingChanged(newValue.intValue()));

        //disable editModeSaveButton if logTotalTimeEditHours or logTotalTimeEditMinutes is empty
        editModeSaveButton.disableProperty().bind(logTotalTimeEditHours.textProperty().isEmpty().or(logTotalTimeEditMinutes.textProperty().isEmpty()));

    }

    private void loadTour(Tour oldTour, Tour newTour){

        editMode.set(false);

        if(oldTour != null) {
            //unbind old tour logs list
            currentTourLogs.unbindBidirectional(oldTour.getTourLogsProperty());
        }

        if(newTour == null) return; //if no tour is selected, do nothing

        currentTourLogs.bindBidirectional(newTour.getTourLogsProperty());

        //bind name to currently selected tour
        tourName.textProperty().bind(newTour.getNameProperty());

    }

    public void onSwitchTabs() {
        tourLogic.getCurrentTabProperty().set(0);
    }

    public void deleteTourLog(TourLog tourLog) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Delete Log");
        alert.setHeaderText("Are you sure you want to delete this log entry?");
        alert.initStyle(StageStyle.TRANSPARENT);

        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType buttonTypeDelete = new ButtonType("Yes", ButtonBar.ButtonData.YES);

        alert.getButtonTypes().setAll(buttonTypeDelete, buttonTypeCancel);

        //add css file to alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(TourPlannerApp.class.getResource("css/confirm-tour-delete.css")).toExternalForm());
        dialogPane.getStyleClass().add("deleteAlertBox");

        //replace alert image with custom image
        Image image = new Image(Objects.requireNonNull(TourPlannerApp.class.getResourceAsStream("images/delete_log.png")));
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
        alert.initOwner(viewModeContainer.getScene().getWindow());

        // show alert box and wait for response
        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeDelete) {
                currentTourLogs.remove(tourLog);
                tourLogic.updateSelectedTourWithoutRecalculating();
            } else if (response == buttonTypeCancel) {
                //do nothing
                //maybe log that the user cancelled deleting the log
            }
        });
    }

    public void startEditMode(TourLog tourLog) {
        tourLogInEdit.set(tourLog);
        logCommentEdit.setText(tourLog.getComment());
        logDifficultyEdit.set(tourLog.getDifficulty());
        logRatingEdit.set(tourLog.getRating());
        logTotalTimeEditHours.setText(String.valueOf(tourLog.getTotalTime().getSeconds() / 3600));
        logTotalTimeEditMinutes.setText(String.valueOf((tourLog.getTotalTime().getSeconds() % 3600) / 60));
        editMode.set(true);
    }

    public void onCancelEdit() {
        editMode.set(false);
    }

    public void onSaveEdit() {

        if(tourLogInEdit.get() == null) { //tour log is new
            TourLog tourLog = new TourLog();
            tourLog.setComment(logCommentEdit.getText());
            tourLog.setDifficulty(logDifficultyEdit.get());
            tourLog.setRating(logRatingEdit.get());
            tourLog.setTotalTime(Duration.ofHours(Integer.parseInt(logTotalTimeEditHours.getText())).plusMinutes(Integer.parseInt(logTotalTimeEditMinutes.getText())));
            tourLog.setDate(new Date());
            currentTourLogs.add(tourLog);
            tourLogic.updateSelectedTourWithoutRecalculating();
            editMode.set(false);
            return;
        }

        TourLog tourLog = tourLogInEdit.get();
        tourLog.setComment(logCommentEdit.getText());
        tourLog.setDifficulty(logDifficultyEdit.get());
        tourLog.setRating(logRatingEdit.get());
        tourLog.setTotalTime(Duration.ofHours(Integer.parseInt(logTotalTimeEditHours.getText())).plusMinutes(Integer.parseInt(logTotalTimeEditMinutes.getText())));
        editMode.set(false);
        tourLogic.updateSelectedTourWithoutRecalculating();
    }

    public void onAddLog() {
        tourLogInEdit.set(null);
        logCommentEdit.setText("");
        logDifficultyEdit.set(Difficulty.MEDIUM);
        logRatingEdit.set(1);
        logTotalTimeEditHours.clear();
        logTotalTimeEditMinutes.clear();
        editMode.set(true);
    }

    private void onEditRatingChanged(int newRating) {
        ratingStar1.getStyleClass().remove("active-rating-star-edit");
        ratingStar2.getStyleClass().remove("active-rating-star-edit");
        ratingStar3.getStyleClass().remove("active-rating-star-edit");
        ratingStar4.getStyleClass().remove("active-rating-star-edit");
        ratingStar5.getStyleClass().remove("active-rating-star-edit");

        if(newRating >= 1) ratingStar1.getStyleClass().add("active-rating-star-edit");
        if(newRating >= 2) ratingStar2.getStyleClass().add("active-rating-star-edit");
        if(newRating >= 3) ratingStar3.getStyleClass().add("active-rating-star-edit");
        if(newRating >= 4) ratingStar4.getStyleClass().add("active-rating-star-edit");
        if(newRating >= 5) ratingStar5.getStyleClass().add("active-rating-star-edit");

    }

    public void onSetRatingEdit1() {
        logRatingEdit.set(1);
    }
    public void onSetRatingEdit2() {
        logRatingEdit.set(2);
    }
    public void onSetRatingEdit3() {
        logRatingEdit.set(3);
    }
    public void onSetRatingEdit4() {
        logRatingEdit.set(4);
    }
    public void onSetRatingEdit5() {
        logRatingEdit.set(5);
    }

    private void onEditDifficultyChanged(Difficulty newDifficulty) {
        difficultySelectorEasy.getStyleClass().clear();
        difficultySelectorMedium.getStyleClass().clear();
        difficultySelectorHard.getStyleClass().clear();
        difficultySelectorExpert.getStyleClass().clear();

        switch (newDifficulty) {
            case EASY -> difficultySelectorEasy.getStyleClass().add("selectedDifficulty");
            case MEDIUM -> difficultySelectorMedium.getStyleClass().add("selectedDifficulty");
            case HARD -> difficultySelectorHard.getStyleClass().add("selectedDifficulty");
            case EXPERT -> difficultySelectorExpert.getStyleClass().add("selectedDifficulty");
        }
    }

    public void changeDifficultyToEasy() {
        logDifficultyEdit.set(Difficulty.EASY);
    }
    public void changeDifficultyToMedium() {
        logDifficultyEdit.set(Difficulty.MEDIUM);
    }
    public void changeDifficultyToHard() {
        logDifficultyEdit.set(Difficulty.HARD);
    }
    public void changeDifficultyToExpert() {
        logDifficultyEdit.set(Difficulty.EXPERT);
    }


}
