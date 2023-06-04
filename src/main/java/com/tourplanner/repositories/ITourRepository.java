package com.tourplanner.repositories;

import com.tourplanner.models.Tour;

import java.util.ArrayList;

public interface ITourRepository {
    void save(Tour tour);

    void update(Tour tour);

    void delete(Tour tour);

    ArrayList<Tour> getAll();

    Tour getById(long id);
}
