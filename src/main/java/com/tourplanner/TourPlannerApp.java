package com.tourplanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class TourPlannerApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TourPlannerApp.class.getResource("views/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
//      Scene scene = new Scene(fxmlLoader.load(), 700, 700);
        //scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.DECORATED);
//      stage.getIcons().add(new Image("https://genuinecoder.com/wp-content/uploads/2022/06/genuine_coder-3.png"));
        stage.getIcons().add(new Image(TourPlannerApp.class.getResourceAsStream("images/iconicon.png")));
        stage.setTitle("Tour Planner");
        stage.setScene(scene);
        //maximize window
        stage.setMaximized(true);
        stage.setMinHeight(800);
        stage.setMinWidth(950);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}