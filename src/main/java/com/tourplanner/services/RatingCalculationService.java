package com.tourplanner.services;

import com.tourplanner.models.Tour;
import com.tourplanner.models.TourLog;

public class RatingCalculationService {

    public void calculateRating(Tour tour){
        if(tour.getTourLogs().isEmpty()){
            tour.setRating(0);
        }

        int sum = 0;
        for (TourLog log : tour.getTourLogs()) {
            sum += log.getRating();
        }
        double average = (double) sum / tour.getTourLogs().size();
        tour.setRating((int) Math.round(average));
    }
}
