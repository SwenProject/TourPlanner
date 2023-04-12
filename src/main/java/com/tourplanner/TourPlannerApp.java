package com.tourplanner;

import com.tourplanner.repositories.TourRepository;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

public class TourPlannerApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TourPlannerApp.class.getResource("views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        // Scene scene = new Scene(fxmlLoader.load(), 700, 700);
        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("TourPlanner");
        stage.setScene(scene);
        //maximize window
        stage.setMaximized(true);
        stage.setMinHeight(800);
        stage.setMinWidth(950);
        stage.show();

        TourRepository tourRepository = new TourRepository();

    }

    public static void main(String[] args) {
        launch();
    }
}