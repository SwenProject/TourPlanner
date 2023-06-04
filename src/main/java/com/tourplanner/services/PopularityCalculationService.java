package com.tourplanner.services;

import com.tourplanner.models.Tour;

public class PopularityCalculationService {

    //calculate the rating of a tour based on the number of tour logs
    public void calculatePopularity(Tour tour){

        int tourLogSize = tour.getTourLogs().size();
        int popularity;

        if (tourLogSize == 0) {
            popularity = 0;
        } else if (tourLogSize < 6) {
            popularity = 1;
        } else if (tourLogSize < 11) {
            popularity = 2;
        } else if (tourLogSize < 16) {
            popularity = 3;
        } else if (tourLogSize < 21) {
            popularity = 4;
        } else {
            popularity = 5;
        }
        tour.setPopularityScore(popularity);
    }
}
