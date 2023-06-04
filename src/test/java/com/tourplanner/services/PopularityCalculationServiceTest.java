package com.tourplanner.services;

import com.tourplanner.models.Tour;
import com.tourplanner.models.TourLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PopularityCalculationServiceTest {

    PopularityCalculationService popularityCalculationService = new PopularityCalculationService();

    @Test
    @DisplayName("Calculate popularity when tour has no logs")
    public void testCalculatePopularityNoLogs() {

        //Arrange
        Tour testTour = new Tour();
        testTour.setTourLogs(new ArrayList<>());

        //Act
        popularityCalculationService.calculatePopularity(testTour);

        //Assert
        assertEquals(0, testTour.getPopularityScore());
    }

    @Test
    @DisplayName("Calculate popularity when tour has one tour log")
    public void testCalculatePopularityOneLog() {

        //Arrange
        Tour testTour = new Tour();
        testTour.setTourLogs(new ArrayList<>());
        testTour.getTourLogs().add(new TourLog());

        //Act
        popularityCalculationService.calculatePopularity(testTour);

        //Assert
        assertEquals(1, testTour.getPopularityScore());
    }

    @Test
    @DisplayName("Calculate popularity when tour has many tour logs")
    public void testCalculatePopularityManyLogs() {

        //Arrange
        Tour testTour = new Tour();
        testTour.setTourLogs(new ArrayList<>());

        //add 15 tour logs
        for (int i = 0; i < 15; i++) {
            testTour.getTourLogs().add(new TourLog());
        }

        //Act
        popularityCalculationService.calculatePopularity(testTour);

        //Assert
        assertEquals(3, testTour.getPopularityScore());
    }

}