package com.tourplanner.services;

import com.tourplanner.models.Tour;
import com.tourplanner.models.TourLog;

public class PopularityCalculationService {

    //calculate the number of tourlogs of a tour
    public void calculatePopularity(Tour tour){

        int tourLogSize = tour.getTourLogs().size();
        int popularity = 0;

        if (tourLogSize == 0) {
            popularity = 0;
        }
        else if (tourLogSize > 0 && tourLogSize < 6) {
            popularity = 1;
        }
        else if (tourLogSize > 5 && tourLogSize < 11) {
            popularity = 2;
        }
        else if (tourLogSize > 10 && tourLogSize < 16) {
            popularity = 3;
        }
        else if (tourLogSize > 15 && tourLogSize < 21) {
            popularity = 4;
        }
        else if (tourLogSize > 20) {
            popularity = 5;
        }
        tour.setPopularityScore(popularity);
    }
}
