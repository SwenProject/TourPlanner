package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;
import com.tourplanner.logic.TourMapServiceMapQuest;
import com.tourplanner.repositories.TourRepository;
import com.tourplanner.services.*;

public class ControllerFactory {

    private final ConfigurationService config;
    private final TourRepository tourRepository;
    private final ITourMapService tourMapService;
    private final TourLogic tourLogic;
    private final IFileImportExportService fileImportExportService;
    private final PdfService pdfService;

    public ControllerFactory(ConfigurationService config) {
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
