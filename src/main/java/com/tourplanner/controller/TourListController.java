package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;

public class TourListController {
    private final TourLogic tourLogic;

    public TourListController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }
}
