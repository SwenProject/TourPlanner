package com.tourplanner.repositories;

import com.tourplanner.models.Tour;

import java.util.ArrayList;

public interface ITourRepository {
    void persist(Tour tour);

    void delete(Tour tour);

    ArrayList<Tour> getAll();
}
