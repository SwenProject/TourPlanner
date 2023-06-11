package com.tourplanner.services.tasks;

import com.tourplanner.models.Tour;
import com.tourplanner.services.ChildFriendlinessCalculationService;
import com.tourplanner.services.interfaces.ITourMapService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

public class TourMapRequestTask extends Task<Void> {
    private final Tour tour;
    private final ITourMapService tourMapService;
    private final ChildFriendlinessCalculationService childFriendlinessCalculationService = new ChildFriendlinessCalculationService();
    private static final Logger logger = LogManager.getLogger(TourMapRequestTask.class);

    public TourMapRequestTask(Tour tour, ITourMapService tourMapService) {
        this.tour = tour;
        this.tourMapService = tourMapService;
    }

    @Override
    protected Void call() {

        //use Platform.runLater to update properties of tour object (must be done on javafx application thread)
        Platform.runLater(() -> {
            tour.setDistance(-1); //set distance and duration to -1 to indicate that the request is in progress
            tour.setDuration(Duration.ofSeconds(-1));
            tour.setPathToMapImage("loading");

            //set loading properties to true
            tour.getDistanceIsLoadingProperty().set(true);
            tour.getImageIsLoadingProperty().set(true);
        });

        //delete old image from filesystem if valid path
        try {
            if (tour.getPathToMapImage() != null && !tour.getPathToMapImage().equals("error") && !tour.getPathToMapImage().equals("loading")) {
                Files.delete(Paths.get(tour.getPathToMapImage()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            tourMapService.calculateRoute(tour); //make api request
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            logger.warn("Could not calculate route for tour \"" + tour.getName() + "\"");
            Platform.runLater(() -> {
                tour.setDistance(-2); // -2 so that frontend can display error message
                tour.setDuration(Duration.ofSeconds(-2)); // -2 so that frontend can display error message
                tour.setPathToMapImage("error"); // "error" because if null frontend displays loading spinner

                //set loading properties to false
                tour.getDistanceIsLoadingProperty().set(false);
                tour.getImageIsLoadingProperty().set(false);
            });
        }

        Platform.runLater(() -> {
            childFriendlinessCalculationService.calculateChildFriendliness(tour); //calculate child friendliness
        });

        return null;
    }
}
