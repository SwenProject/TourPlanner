package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;

public class TourInfoController {
    private final TourLogic tourLogic;

    public TourInfoController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }
}
