package com.tourplanner.logic;
import com.tourplanner.enums.TransportType;
import com.tourplanner.models.Tour;

import com.tourplanner.services.ConfigurationService;
import com.tourplanner.services.TourMapServiceMapQuest;
import com.tourplanner.services.interfaces.IConfigurationService;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class TourMapServiceMapQuestTest {

    static TourMapServiceMapQuest tourMapServiceMapQuest;

    @BeforeAll
    static void setUp() throws IOException {

        Platform.startup(() -> {}); //needed for JavaFX Platform.runLater() to work in unit tests

        //load endpoints and api key from config.properties
        IConfigurationService config = new ConfigurationService("config.properties");
        config.checkConfig();

        tourMapServiceMapQuest = new TourMapServiceMapQuest(config);
    }

    @Test
    @DisplayName("calculate route from Vienna to Graz with car")
    void calculateRouteViennaToGrazWithCar() {

        //arrange
        Tour tour = new Tour();
        tour.setName("SuperTour");
        tour.setStartingPoint("Wien");
        tour.setDestinationPoint("Graz");
        tour.setTransportType(TransportType.CAR);

        try {
            //act
            tourMapServiceMapQuest.calculateRoute(tour);

            //assert
            //check that calculated values are in expected range
            assertTrue(tour.getDistance() > 190 && tour.getDistance() < 210);
            assertTrue(tour.getDuration().getSeconds() > 6500.00 && tour.getDuration().getSeconds() < 7500.00);

            //check that image file was created
            File file = new File(tour.getPathToMapImage());
            assertTrue(file.exists());
        } finally {
            deleteFile(tour.getPathToMapImage());
        }
    }

    @Test
    @DisplayName("calculate route from Vienna to Graz by foot")
    void calculateRouteViennaToGrazByFoot() {

        //arrange
        Tour tour = new Tour();
        tour.setName("SuperTour");
        tour.setStartingPoint("Wien");
        tour.setDestinationPoint("Graz");
        tour.setTransportType(TransportType.FEET);

        try {
            //act
            tourMapServiceMapQuest.calculateRoute(tour);

            //assert
            //check that duration is way higher than by car
            assertTrue(tour.getDistance() > 170 && tour.getDistance() < 190);
            assertTrue((double) tour.getDuration().getSeconds() / 3600 > 20);

            //check that image file was created
            File file = new File(tour.getPathToMapImage());
            assertTrue(file.exists());
        } finally {
            deleteFile(tour.getPathToMapImage());
        }
    }

    @Test
    @DisplayName("calculate route from Vienna to Graz by bike")
    void calculateRouteViennaToGrazByBike() {

        //arrange
        Tour tour = new Tour();
        tour.setName("SuperTour");
        tour.setStartingPoint("Wien");
        tour.setDestinationPoint("Graz");
        tour.setTransportType(TransportType.BIKE);

        try {
            //act
            tourMapServiceMapQuest.calculateRoute(tour);

            //assert
            //check that duration is higher than by car but lower than by foot
            assertTrue(tour.getDistance() > 170 && tour.getDistance() < 190);
            assertTrue((double) tour.getDuration().getSeconds() / 3600 > 10);

            //check that image file was created
            File file = new File(tour.getPathToMapImage());
            assertTrue(file.exists());
        } finally {
            deleteFile(tour.getPathToMapImage());
        }
    }

    @Test
    @DisplayName("calculate route with specific addresses")
    void calculateRouteSpecificAddresses() {

        //arrange
        Tour tour = new Tour();
        tour.setName("SuperTour");
        tour.setStartingPoint("Höchstädtplatz 6, 1200 Wien"); //FH Technikum Wien
        tour.setDestinationPoint("Gaudeegasse 1, 1020 Wien"); //Vienna Giant Ferris Wheel
        tour.setTransportType(TransportType.BIKE);

        try {
            //act
            tourMapServiceMapQuest.calculateRoute(tour);

            //assert
            //check that values are in expected range
            assertTrue(tour.getDistance() > 3 && tour.getDistance() < 10);
            assertTrue(tour.getDuration().getSeconds() > 1000 && tour.getDuration().getSeconds() < 2000);

            //check that image file was created
            File file = new File(tour.getPathToMapImage());
            assertTrue(file.exists());
        } finally {
            deleteFile(tour.getPathToMapImage());
        }
    }

    @Test
    @DisplayName("calculate route with coordinates instead of addresses")
    void calculateRouteCoordinates() {

        //arrange
        Tour tour = new Tour();
        tour.setName("SuperTour");
        tour.setStartingPoint("48.19876767112551, 16.36841351064006"); //TU Wien
        tour.setDestinationPoint("48.213335579324266, 16.359964576334406"); //University of Vienna
        tour.setTransportType(TransportType.CAR);

        try {
            //act
            tourMapServiceMapQuest.calculateRoute(tour);

            //assert
            //check that values are in expected range
            assertTrue(tour.getDistance() > 1 && tour.getDistance() < 5);
            assertTrue(tour.getDuration().getSeconds() > 250 && tour.getDuration().getSeconds() < 500);

            //check that image file was created
            File file = new File(tour.getPathToMapImage());
            assertTrue(file.exists());
        } finally {
            deleteFile(tour.getPathToMapImage());
        }
    }

    private static void deleteFile(String filePath) {
        File file = new File(filePath);

        if (file.exists()) {
            boolean isDeleted = file.delete();

            if (isDeleted) {
                System.out.println("Deleted tour image");
            } else {
                System.out.println("Could not delete tour image");
            }
        } else {
            System.out.println("Could not delete tour image because it does not exist");
        }
    }
}