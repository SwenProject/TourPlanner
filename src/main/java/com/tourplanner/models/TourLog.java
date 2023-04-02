package com.tourplanner.models;

import com.tourplanner.enums.Difficulty;
import java.time.Duration;
import java.util.Date;

public class TourLog {
    int id;
    int rating;
    Difficulty difficulty;
    Duration totalTime;
    String comment;
    Date date;
}
