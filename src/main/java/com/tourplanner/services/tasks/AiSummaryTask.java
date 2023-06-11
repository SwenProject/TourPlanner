package com.tourplanner.services.tasks;

import com.tourplanner.models.Tour;
import com.tourplanner.services.interfaces.IAiSummaryService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import java.io.IOException;
import java.util.function.Consumer;

public class AiSummaryTask extends Task<Void> {
    private final Tour tour;
    private final Consumer<Tour> callback;
    private final IAiSummaryService aiSummaryService;

    private static final Logger logger = LogManager.getLogger(TourMapRequestTask.class);

    public AiSummaryTask(Tour tour, IAiSummaryService aiSummaryService, Consumer<Tour> callback) {
        this.tour = tour;
        this.aiSummaryService = aiSummaryService;
        this.callback = callback;
    }

    @Override
    protected Void call() {

        logger.info("Generating AI summary for tour \"" + tour.getName() + "\"");

        Platform.runLater(() -> {
            tour.setAiSummary("");
            tour.getAiSummaryIsLoadingProperty().set(true);
            callback.accept(tour); //update tour in db in case the app is closed before the api request is finished
        });


        try {
            String aiSummary = aiSummaryService.generateAiSummary(tour);

            Platform.runLater(() -> {
                tour.setAiSummary(aiSummary);
                tour.getAiSummaryIsLoadingProperty().set(false);
                callback.accept(tour); //update tour in db using the supplied callback function
            });
            logger.info("AI summary generated");
        } catch (IOException | JSONException e) {
            logger.warn("Could not create AI summary for tour \"" + tour.getName() + "\"");
            Platform.runLater(() -> {
                tour.setAiSummary("");
                tour.getAiSummaryIsLoadingProperty().set(false);
                callback.accept(tour); //update tour in db using the supplied callback function
            });
            e.printStackTrace();
        }

        return null;
    }
}
