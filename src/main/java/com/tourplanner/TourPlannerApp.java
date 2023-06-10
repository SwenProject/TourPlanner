package com.tourplanner;

import com.tourplanner.controller.ControllerFactory;
import com.tourplanner.services.ConfigurationService;
import com.tourplanner.services.interfaces.IConfigurationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.Objects;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;

public class TourPlannerApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        //TODO loading stage - > loading scene (stagestyle undecorated)
        Stage loadingStage = new Stage();
        //Load fxml for loading stage
        FXMLLoader loadingScreenFxmlLoader = new FXMLLoader(TourPlannerApp.class.getResource("views/loading_screen.fxml"));
        Scene loadingScene = new Scene(loadingScreenFxmlLoader.load());
        loadingScene.setFill(Color.TRANSPARENT);

        loadingStage.initStyle(StageStyle.UNDECORATED);
        loadingStage.setScene(loadingScene);
        loadingStage.setAlwaysOnTop(true);
        loadingStage.getIcons().add(new Image(Objects.requireNonNull(TourPlannerApp.class.getResourceAsStream("images/app_icon.png"))));
        loadingStage.show();

        //start actual application later so that loading screen is shown immediately
        Platform.runLater(() -> {
            try {
                // load config
                IConfigurationService config = null;
                config = new ConfigurationService("config.properties");

                //check that all config values are set
                config.checkConfig();

        // set log level for log4j
        Configurator.setRootLevel(Level.toLevel(config.getStringConfig("log.logLevel")));

        FXMLLoader fxmlLoader = new FXMLLoader(TourPlannerApp.class.getResource("views/main-view.fxml"));

        ControllerFactory controllerFactory = new ControllerFactory(config);
        fxmlLoader.setControllerFactory(controllerFactory::create); //set ControllerFactory for fxmlLoader

                Scene scene = new Scene(fxmlLoader.load());

                stage.initStyle(StageStyle.DECORATED);
                stage.getIcons().add(new Image(Objects.requireNonNull(TourPlannerApp.class.getResourceAsStream("images/app_icon.png"))));
                stage.setTitle("Tour Planner");
                stage.setScene(scene);
                stage.setMaximized(true);
                stage.setMinHeight(800);
                stage.setMinWidth(950);

                stage.show(); //show actual stage
                Thread.sleep(500); //we wait a bit before closing the loading stage
                //loadingStage.hide();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}