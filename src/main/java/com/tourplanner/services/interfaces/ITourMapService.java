package com.tourplanner.services.interfaces;

import com.tourplanner.models.Tour;
import org.json.JSONException;

import java.io.IOException;

public interface ITourMapService {
    void calculateRoute(Tour tour) throws JSONException, IOException;
}
