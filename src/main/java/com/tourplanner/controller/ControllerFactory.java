package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;

public class ControllerFactory {
    private final TourLogic tourLogic = new TourLogic();

    public Object create(Class<?> controllerClass){
        if(controllerClass == MainController.class){
            return new MainController(tourLogic);
        } else if (controllerClass == MapController.class){
            return new MapController(tourLogic);
        } else if (controllerClass == NewImportExportController.class){
            return new NewImportExportController(tourLogic);
        } else if (controllerClass == SearchBoxController.class){
            return new SearchBoxController(tourLogic);
        } else if (controllerClass == TourInfoController.class){
            return new TourInfoController(tourLogic);
        } else if (controllerClass == TourListController.class){
            return new TourListController(tourLogic);
        }
        throw new IllegalArgumentException("ControllerFactory was not able to create a controller for the class: " + controllerClass.getName() + "!");
    }
}
