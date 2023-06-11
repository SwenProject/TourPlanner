package com.tourplanner;

import javafx.application.Preloader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;

public class TourPlannerPreloader extends Preloader {

    @FXML
    public ProgressBar progressBar;
    private Stage preloaderStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        preloaderStage = primaryStage;

        //Load fxml for loading stage
        FXMLLoader loadingScreenFxmlLoader = new FXMLLoader(TourPlannerApp.class.getResource("views/loading_screen.fxml"));

        //set this as controller for loading screen
        loadingScreenFxmlLoader.setController(this);

        Scene loadingScene = new Scene(loadingScreenFxmlLoader.load());

        preloaderStage.initStyle(StageStyle.UNDECORATED);
        preloaderStage.setScene(loadingScene);
        preloaderStage.setAlwaysOnTop(true);
        preloaderStage.getIcons().add(new Image(Objects.requireNonNull(TourPlannerApp.class.getResourceAsStream("images/app_icon.png"))));
        preloaderStage.show();
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification pn) {
            progressBar.setProgress(pn.getProgress());
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        if (info.getType() == StateChangeNotification.Type.BEFORE_START) {
            preloaderStage.hide();
        }
    }
}