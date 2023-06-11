package com.tourplanner.services.tasks;

import com.tourplanner.models.Tour;
import com.tourplanner.services.interfaces.IAiSummaryService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;

import java.io.IOException;

public class AiSummaryTask extends Task<Void> {
    private final Tour tour;
    private final IAiSummaryService aiSummaryService;

    private static final Logger logger = LogManager.getLogger(TourMapRequestTask.class);

    public AiSummaryTask(Tour tour, IAiSummaryService aiSummaryService) {
        this.tour = tour;
        this.aiSummaryService = aiSummaryService;
    }

    @Override
    protected Void call() {

        logger.info("Generating AI summary for tour \"" + tour.getName() + "\"");

        Platform.runLater(() -> {
            tour.setAiSummary("");
            tour.getAiSummaryIsLoadingProperty().set(true);
        });


        try {
            String aiSummary = aiSummaryService.generateAiSummary(tour);

            Platform.runLater(() -> {
                tour.setAiSummary(aiSummary);
                tour.getAiSummaryIsLoadingProperty().set(false);
            });
            logger.info("AI summary generated");
        } catch (IOException | JSONException e) {
            logger.warn("Could not create AI summary for tour \"" + tour.getName() + "\"");
            Platform.runLater(() -> {
                tour.setAiSummary("");
                tour.getAiSummaryIsLoadingProperty().set(false);
            });
            e.printStackTrace();
        }

        return null;
    }
}
