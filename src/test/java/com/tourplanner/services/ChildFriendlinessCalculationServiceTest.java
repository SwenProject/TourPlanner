package com.tourplanner.services;

import com.tourplanner.enums.Difficulty;
import com.tourplanner.enums.TransportType;
import com.tourplanner.models.Tour;
import com.tourplanner.models.TourLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ChildFriendlinessCalculationServiceTest {

    ChildFriendlinessCalculationService childFriendlinessCalculationService = new ChildFriendlinessCalculationService();

    @Test
    @DisplayName("Calculate child friendliness on car tour")
    public void testCalculateChildFriendlinessCarTour() {

        //Arrange
        Tour testTour = new Tour();
        testTour.setTourLogs(new ArrayList<>());
        testTour.setTransportType(TransportType.CAR);

        //Act
        childFriendlinessCalculationService.calculateChildFriendliness(testTour);

        //Assert
        assertEquals(1, testTour.getChildFriendlinessScore()); //car tours should always be marked as child-friendly
    }

    @Test
    @DisplayName("Calculate child friendliness on short tour with no tour logs")
    public void testCalculateChildFriendlinessShortTour() {

        //Arrange
        Tour testTour = new Tour();
        testTour.setTourLogs(new ArrayList<>());
        testTour.setTransportType(TransportType.FEET);
        testTour.setDistance(1);

        //Act
        childFriendlinessCalculationService.calculateChildFriendliness(testTour);

        //Assert
        assertEquals(1, testTour.getChildFriendlinessScore()); //short tours should be marked as child-friendly if there are no logs
    }

    @Test
    @DisplayName("Calculate child friendliness on short tour with high difficulty")
    public void testCalculateChildFriendlinessShortTourHighDifficulty() {

        //Arrange
        Tour testTour = new Tour();
        testTour.setTourLogs(new ArrayList<>());

        TourLog testTourLog = new TourLog();
        testTourLog.setDifficulty(Difficulty.EXPERT);
        testTour.getTourLogs().add(testTourLog);

        testTour.setTransportType(TransportType.FEET);
        testTour.setDistance(1);

        //Act
        childFriendlinessCalculationService.calculateChildFriendliness(testTour);

        //Assert
        assertEquals(0, testTour.getChildFriendlinessScore()); //if the tour is short but has a high difficulty, it should not be marked as child-friendly
    }

    @Test
    @DisplayName("Calculate child friendliness on long tour")
    public void testCalculateChildFriendlinessLongTour() {

        //Arrange
        Tour testTour = new Tour();
        testTour.setTourLogs(new ArrayList<>());
        testTour.setTransportType(TransportType.FEET);
        testTour.setDistance(20);

        //Act
        childFriendlinessCalculationService.calculateChildFriendliness(testTour);

        //Assert
        assertEquals(0, testTour.getChildFriendlinessScore()); //if the tour is too long, it should not be marked as child-friendly
    }

    @Test
    @DisplayName("Calculate child friendliness on tour with high average total time")
    public void testCalculateChildFriendlinessHighTotalTime() {

        //Arrange
        Tour testTour = new Tour();
        testTour.setTourLogs(new ArrayList<>());

        TourLog testTourLog = new TourLog();
        testTourLog.setTotalTime(Duration.ofHours(5));
        testTourLog.setDifficulty(Difficulty.EASY);
        testTour.getTourLogs().add(testTourLog);

        TourLog testTourLog2 = new TourLog();
        testTourLog2.setTotalTime(Duration.ofHours(2));
        testTourLog2.setDifficulty(Difficulty.EASY);
        testTour.getTourLogs().add(testTourLog2);

        testTour.setTransportType(TransportType.FEET);
        testTour.setDistance(1);

        //Act
        childFriendlinessCalculationService.calculateChildFriendliness(testTour);

        //Assert
        assertEquals(0, testTour.getChildFriendlinessScore()); //if the average total time is too high, it should not be marked as child-friendly
    }

}