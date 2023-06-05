package com.tourplanner.services;

import com.tourplanner.enums.TransportType;
import com.tourplanner.models.Tour;
import com.tourplanner.models.TourLog;

public class ChildFriendlinessCalculationService {


    private double calculateAverageDifficulty(Tour tour){

        double averageDifficulty = 0;

        //calculate the average difficulty of all tourlogs
        for (TourLog log : tour.getTourLogs()) {
            averageDifficulty += log.getDifficulty().ordinal(); //ordinal: changes the enum to an int
        }
        return averageDifficulty / tour.getTourLogs().size();
    }

    private double calculateAverageTotalTime(Tour tour){
        double averageTotalTime = 0;

        //calculate the average total time of all tourlogs
        for (TourLog log : tour.getTourLogs()) {
            averageTotalTime += log.getTotalTime().getSeconds();
        }
        return averageTotalTime / tour.getTourLogs().size();
    }

    public void calculateChildFriendliness(Tour tour) {

        if(tour.getDistance() == -1 || tour.getDistance() == -2){
            tour.setChildFriendlinessScore(-1); // if the distance is not calculated, the child-friendliness is not calculated
            return;
        }

        //calculate the child friendliness of a tour by using the average difficulty and the average total time and the distance
        if (tour.getTransportType() == TransportType.CAR) { //when it is a car tour, it is child-friendly
            tour.setChildFriendlinessScore(1);
        } else if (calculateAverageDifficulty(tour) > 1) { // if the difficulty is higher than medium, the tour is not child-friendly
            tour.setChildFriendlinessScore(0);

        // if the difficulty is higher than medium, the tour is not child-friendly
        } else if (calculateAverageDifficulty(tour) > Difficulty.MEDIUM.ordinal()) {
            tour.setChildFriendlinessScore(0); // enum: hard and expert

        // if the distance is higher than 5km or the average total time is higher than 3 hours and the transport type is feet, the tour is not child-friendly
        } else if ((tour.getDistance() > 5 || calculateAverageTotalTime(tour) > 3 * 3600) && tour.getTransportType() == TransportType.FEET) {
            tour.setChildFriendlinessScore(0);

        // if the distance is higher than 10km or the average total time is higher than 3 hours and the transport type is bike, the tour is not child-friendly
        } else if ((tour.getDistance() > 10 || calculateAverageTotalTime(tour) > 3 * 3600) && tour.getTransportType() == TransportType.BIKE) {
            tour.setChildFriendlinessScore(0);
        } else {
            tour.setChildFriendlinessScore(1); // else the tour is child-friendly
        }

    }
}
