package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;
import com.tourplanner.services.TourMapServiceMapQuest;
import com.tourplanner.repositories.ITourRepository;
import com.tourplanner.repositories.TourRepository;
import com.tourplanner.services.*;
import com.tourplanner.services.interfaces.IConfigurationService;
import com.tourplanner.services.interfaces.IFileImportExportService;
import com.tourplanner.services.interfaces.ITourMapService;

public class ControllerFactory {

    //interfaces for dependency injection
    private final IConfigurationService config;
    private final ITourRepository tourRepository;
    private final ITourMapService tourMapService;
    private final IFileImportExportService fileImportExportService;
    private final TourLogic tourLogic;
    private final PdfService pdfService;

    public ControllerFactory(IConfigurationService config) {
        this.config = config;
        this.tourRepository = new TourRepository(config);
        this.tourMapService = new TourMapServiceMapQuest(config);
        this.tourLogic = new TourLogic(tourRepository, tourMapService);
        this.fileImportExportService = new FileImportExportService(tourLogic);
        this.pdfService = new PdfService();
    }

    public Object create(Class<?> controllerClass){
        if(controllerClass == MainController.class){
            return new MainController(tourLogic);
        } else if (controllerClass == MapController.class){
            return new MapController(tourLogic);
        } else if (controllerClass == NewImportExportController.class){
            return new NewImportExportController(tourLogic, config, fileImportExportService, pdfService);
        } else if (controllerClass == SearchBoxController.class){
            return new SearchBoxController(tourLogic);
        } else if (controllerClass == TourInfoController.class){
            return new TourInfoController(tourLogic, pdfService);
        } else if (controllerClass == TourListController.class){
            return new TourListController(tourLogic);
        } else if (controllerClass == TourLogsController.class){
            return new TourLogsController(tourLogic);
        } else if (controllerClass == TourDetailsController.class) {
            return new TourDetailsController(tourLogic);
        }
        throw new IllegalArgumentException("ControllerFactory was not able to create a controller for the class: " + controllerClass.getName() + "!");
    }
}
