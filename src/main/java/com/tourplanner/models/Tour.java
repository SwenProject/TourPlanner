package com.tourplanner.models;

import com.tourplanner.enums.TransportType;

import java.time.Duration;
import java.util.List;

public class Tour {
    int id;
    String name;
    String startingPoint;
    String destinationPoint;
    String description;
    double distance;
    Duration duration;
    int rating;
    TransportType transportType; //enum: car, bike, train, feet
    String pathToMapImage;
    List<TourLog> tourLogs;

}
