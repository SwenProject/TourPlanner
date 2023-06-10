package com.tourplanner.services.tasks;

import com.tourplanner.models.Tour;
import com.tourplanner.services.ChildFriendlinessCalculationService;
import com.tourplanner.services.interfaces.ITourMapService;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.time.Duration;
import java.util.function.Consumer;

public class TourMapRequestTask extends Task<Void> {
    private final Tour tour;
    private Consumer<Tour> callback;
    private final ITourMapService tourMapService;
    private final ChildFriendlinessCalculationService childFriendlinessCalculationService = new ChildFriendlinessCalculationService();


    public TourMapRequestTask(Tour tour, ITourMapService tourMapService) {
        this.tour = tour;
        this.tourMapService = tourMapService;
    }

    public void setCallback(Consumer<Tour> callback) {
        this.callback = callback;
    }

    @Override
    protected Void call() {

        //set to -1 before actual request so that loading spinner immediately appears (even when waiting for other request to complete
        Platform.runLater(() -> {
            tour.setDistance(-1); //set distance and duration to -1 to indicate that the request is in progress
            tour.setDuration(Duration.ofSeconds(-1));
        });

        tourMapService.calculateRoute(tour); //make api request

        Platform.runLater(() -> {
            childFriendlinessCalculationService.calculateChildFriendliness(tour); //calculate child friendliness
        });

        callback.accept(tour); //update tour in db using the supplied callback function

        return null;
    }
}
