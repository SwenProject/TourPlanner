package com.tourplanner.services.interfaces;

import com.tourplanner.models.Tour;
import org.json.JSONException;

import java.io.IOException;

public interface IAiSummaryService {
    String generateAiSummary(Tour tour) throws JSONException, IOException;
}
