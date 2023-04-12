package com.tourplanner;

import com.tourplanner.enums.Difficulty;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.persistence.*;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Duration;

import com.tourplanner.models.*;

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

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("tourPU");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        // create demo tour
        Tour tour1 = new Tour();
        tour1.setName("Tour 1");
        tour1.setDistance(12.5);
        tour1.setStartingPoint("Starting Point");
        tour1.setDestinationPoint("Destination Point");

        TourLog tourLog1 = new TourLog();
        tourLog1.setRating(5);
        tourLog1.setDifficulty(Difficulty.EASY);
        tourLog1.setTotalTime(Duration.ofHours(2));

        TourLog tourLog2 = new TourLog();
        tourLog2.setRating(5);
        tourLog2.setDifficulty(Difficulty.EASY);
        tourLog2.setTotalTime(Duration.ofHours(2));

        tour1.getTourLogs().add(tourLog1);
        tour1.getTourLogs().add(tourLog2);


        entityManager.persist(tour1);

        entityManager.getTransaction().commit();

        //print new tour id
        System.out.println("Tour ID: " + tour1.getId());

        entityManager.close();
        entityManagerFactory.close();

    }

    public static void main(String[] args) {
        launch();
    }
}