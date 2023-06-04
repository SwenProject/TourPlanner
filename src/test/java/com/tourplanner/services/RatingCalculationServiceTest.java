package com.tourplanner.services;

import com.tourplanner.models.TourLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tourplanner.models.Tour;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


class RatingCalculationServiceTest {

    RatingCalculationService ratingCalculationService = new RatingCalculationService();

    @Test
    @DisplayName("Calculate rating when tour has no logs")
    public void testCalculateRatingNoLogs() {

        //Arrange
        Tour testTour = new Tour();
        testTour.setTourLogs(new ArrayList<>());

        //Act
        ratingCalculationService.calculateRating(testTour);

        //Assert
        assertEquals(0, testTour.getRating());

    }

    @Test
    @DisplayName("Calculate rating when tour has one log")
    public void testCalculateRatingOneLog() {

        //Arrange
        Tour testTour = new Tour();
        testTour.setTourLogs(new ArrayList<>());

        TourLog testTourLog = new TourLog();
        testTourLog.setRating(5);

        testTour.getTourLogs().add(testTourLog);

        //Act
        ratingCalculationService.calculateRating(testTour);

        //Assert
        assertEquals(5, testTour.getRating());
    }

    @Test
    @DisplayName("Calculate average rating when tour has multiple logs")
    public void testCalculateRatingMultipleLogs() {

        //Arrange
        Tour testTour = new Tour();
        testTour.setTourLogs(new ArrayList<>());

        TourLog testTourLog1 = new TourLog();
        testTourLog1.setRating(5);

        TourLog testTourLog2 = new TourLog();
        testTourLog2.setRating(1);

        TourLog testTourLog3 = new TourLog();
        testTourLog3.setRating(3);

        testTour.getTourLogs().add(testTourLog1);
        testTour.getTourLogs().add(testTourLog2);
        testTour.getTourLogs().add(testTourLog3);

        //Act
        ratingCalculationService.calculateRating(testTour);

        //Assert
        assertEquals(3, testTour.getRating());
    }

}