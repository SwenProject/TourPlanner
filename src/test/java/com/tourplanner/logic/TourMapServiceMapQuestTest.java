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

        //act
        tourMapServiceMapQuest.calculateRoute(tour);


    }
}