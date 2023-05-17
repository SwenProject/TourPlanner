package com.tourplanner.services;

import com.tourplanner.models.Tour;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

public class TourMapRequestTask extends Task<Void> {
    private final Tour tour;
    private Consumer<Tour> callback;
    private final ITourMapService tourMapService;
    private final Semaphore semaphore;

    public TourMapRequestTask(Tour tour, ITourMapService tourMapService, Semaphore semaphore) {
        this.tour = tour;
        this.tourMapService = tourMapService;
        this.semaphore = semaphore;
    }

    public void setCallback(Consumer<Tour> callback) {
        this.callback = callback;
    }

    @Override
    protected Void call() {
        try {

            //set to -1 before actual request so that loading spinner immediately appears (even when waiting for other request to complete
            Platform.runLater(() -> {
                tour.setDistance(-1); //set distance and duration to -1 to indicate that the request is in progress
                tour.setDuration(Duration.ofSeconds(-1));
            });

            semaphore.acquire(); //wait for any previous api requests to complete
            tourMapService.calculateRoute(tour); //make api request
            callback.accept(tour); //update tour in db using the supplied callback function
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release(); //release semaphore (even if exception is thrown)
        }
        return null;
    }
}
