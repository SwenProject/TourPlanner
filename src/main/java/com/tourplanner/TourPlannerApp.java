package com.tourplanner;

import com.tourplanner.controller.ControllerFactory;
import com.tourplanner.services.ConfigurationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

public class TourPlannerApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // load config
        ConfigurationService config = new ConfigurationService("config.properties");
        //check that all config values are set
        config.checkConfig();

        // set log level for log4j
        Configurator.setRootLevel(Level.toLevel(config.getStringConfig("log.logLevel")));

        FXMLLoader fxmlLoader = new FXMLLoader(TourPlannerApp.class.getResource("views/main-view.fxml"));

        ControllerFactory controllerFactory = new ControllerFactory(config);
        fxmlLoader.setControllerFactory(controllerFactory::create); //set ControllerFactory for fxmlLoader

        Scene scene = new Scene(fxmlLoader.load());
        // Scene scene = new Scene(fxmlLoader.load(), 700, 700);
        // scene.setFill(Color.TRANSPARENT);
        // Scene scene = new Scene(fxmlLoader.load(), 700, 700);

        stage.initStyle(StageStyle.DECORATED);
        stage.getIcons().add(new Image(TourPlannerApp.class.getResourceAsStream("images/app_icon.png")));
        stage.setTitle("Tour Planner");
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