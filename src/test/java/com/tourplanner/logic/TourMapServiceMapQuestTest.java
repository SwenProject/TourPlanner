package com.tourplanner.logic;
import com.tourplanner.models.Tour;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TourMapServiceMapQuestTest {
    Tour tour;
    TourMapServiceMapQuest tourMapServiceMapQuest = new TourMapServiceMapQuest();

    @BeforeEach
    void setUp(){
        tour = new Tour();

    }
    @Test
    void calculateRoute() {
        //arrange
        tour.setName("SuperTour");
        tour.setStartingPoint("Wien");
        tour.setDestinationPoint("Graz");
        //tour.getTransportType()

        //act
        tourMapServiceMapQuest.calculateRoute(tour);

        //assert
        assertTrue(tour.getDistance() > 190 && tour.getDistance() < 210);
        assertTrue(tour.getDuration().getSeconds() > 6500.00 && tour.getDuration().getSeconds() < 7500.00);


    }
}