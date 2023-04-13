package com.tourplanner;

import com.tourplanner.controller.ControllerFactory;
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

        ControllerFactory controllerFactory = new ControllerFactory();
        fxmlLoader.setControllerFactory(controllerFactory::create); //set ControllerFactory for fxmlLoader

        Scene scene = new Scene(fxmlLoader.load());
        // Scene scene = new Scene(fxmlLoader.load(), 700, 700);

        stage.initStyle(StageStyle.DECORATED);
        stage.setTitle("TourPlanner");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setMinHeight(800);
        stage.setMinWidth(950);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}