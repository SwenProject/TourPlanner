package com.tourplanner;

import com.tourplanner.controller.ControllerFactory;
import com.tourplanner.services.ConfigurationService;
import com.tourplanner.services.interfaces.IConfigurationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.Objects;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;


public class TourPlannerApp extends Application {

    Scene mainScene;

    public static void main(String[] args) {
        System.setProperty("javafx.preloader", TourPlannerPreloader.class.getCanonicalName());
        launch(args);
    }

    @Override
    public void init() throws Exception {

        //load config
        IConfigurationService config = new ConfigurationService("config.properties");
        config.checkConfig(); //check that all config values are set

        notifyPreloader(new TourPlannerPreloader.ProgressNotification(0.1));

        // set log level for log4j
        Configurator.setRootLevel(Level.toLevel(config.getStringConfig("log.logLevel")));

        notifyPreloader(new TourPlannerPreloader.ProgressNotification(0.2));

        FXMLLoader fxmlLoader = new FXMLLoader(TourPlannerApp.class.getResource("views/main-view.fxml"));

        notifyPreloader(new TourPlannerPreloader.ProgressNotification(0.3));

        //fake loading progress while constructing ControllerFactory
        Thread thread = new Thread(() -> {
            try {
                for(double i = 0.3; i < 0.9; i += 0.1) {
                    notifyPreloader(new TourPlannerPreloader.ProgressNotification(i));
                    Thread.sleep(700);
                }
            } catch (InterruptedException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

        ControllerFactory controllerFactory = new ControllerFactory(config);
        fxmlLoader.setControllerFactory(controllerFactory::create); //set ControllerFactory for fxmlLoader

        thread.interrupt();
        notifyPreloader(new TourPlannerPreloader.ProgressNotification(1));

        this.mainScene = new Scene(fxmlLoader.load());
    }

    @Override
    public void start(Stage stage) throws IOException {
        stage.initStyle(StageStyle.DECORATED);
        stage.getIcons().add(new Image(Objects.requireNonNull(TourPlannerApp.class.getResourceAsStream("images/app_icon.png"))));
        stage.setTitle("Tour Planner");
        stage.setScene(this.mainScene);
        stage.setMaximized(true);
        stage.setMinHeight(800);
        stage.setMinWidth(950);

        //add event handler for F11 key to toggle fullscreen
        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (KeyCode.F11.equals(event.getCode())) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        stage.show();

        //set always on top to true and then false so that the window is always focused when the app starts
        stage.setAlwaysOnTop(true);
        stage.setAlwaysOnTop(false);
    }

}