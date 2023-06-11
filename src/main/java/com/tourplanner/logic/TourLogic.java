package com.tourplanner.logic;

import com.tourplanner.enums.TransportType;
import com.tourplanner.models.Tour;
import com.tourplanner.repositories.ITourRepository;
import com.tourplanner.services.*;
import com.tourplanner.services.interfaces.IAiSummaryService;
import com.tourplanner.services.interfaces.ITourMapService;
import com.tourplanner.services.tasks.AiSummaryTask;
import com.tourplanner.services.tasks.TourMapRequestTask;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class TourLogic {
    private final ITourRepository tourRepository;
    private final ITourMapService tourMapService;
    private final IAiSummaryService aiSummaryService;
    private final ObservableList<Tour> allTours = FXCollections.observableArrayList();
    private final FilteredList<Tour> searchedTours = new FilteredList<>(allTours);
    private final ListProperty<Tour> searchedToursListProperty = new SimpleListProperty<>(searchedTours);
    private final ObjectProperty<Tour> selectedTourProperty = new SimpleObjectProperty<>();
    private final IntegerProperty currentTabProperty = new SimpleIntegerProperty(0);
    private final RatingCalculationService ratingCalculationService = new RatingCalculationService();
    private final PopularityCalculationService popularityCalculationService = new PopularityCalculationService();
    private final ChildFriendlinessCalculationService childFriendlinessCalculationService = new ChildFriendlinessCalculationService();

    private static final Logger logger = LogManager.getLogger(TourLogic.class);

    public TourLogic(ITourRepository tourRepository, ITourMapService tourMapService, IAiSummaryService aiSummaryService) {
        this.tourRepository = tourRepository;
        this.tourMapService = tourMapService;
        this.aiSummaryService = aiSummaryService;

        allTours.setAll(this.tourRepository.getAll()); //load all tours from db in constructor
    }

    public ObservableList<Tour> getAllToursList() {
        return allTours;
    }

    public FilteredList<Tour> getSearchedToursList() {
        return searchedTours;
    }

    public ListProperty<Tour> getSearchedToursListProperty() {
        return searchedToursListProperty;
    }

    public ObjectProperty<Tour> getSelectedTourProperty() {
        return selectedTourProperty;
    }

    public IntegerProperty getCurrentTabProperty() {
        return currentTabProperty;
    }

    public void selectTour(Tour tour){
        selectedTourProperty.set(tour);
    }

    public void createNewTour(){

        logger.info("Creating new tour");

        Tour newTour = new Tour();

        //set default selected Transport Type
        newTour.setTransportType(TransportType.CAR);

        //mark tour as new
        newTour.setIsNew(true);

        selectedTourProperty.set(newTour);
    }

    public void recalculateTour(Tour tour){

        if(tour.isNew()){
            tourRepository.persist(tour);
        }

        this.ratingCalculationService.calculateRating(tour);
        this.popularityCalculationService.calculatePopularity(tour);

        //create new task for api request (to run async)
        TourMapRequestTask tourMapRequestTask = new TourMapRequestTask(tour, tourMapService); //create new task

        if(tour.isNew()) {
            tour.setIsNew(false); //tour is not new anymore
            allTours.add(tour); //add new tour to all tours list
        }

        Thread thread = new Thread(tourMapRequestTask);
        thread.setDaemon(true); //abort request if ui is closed
        thread.start();
    }

    public void recalculateTourWithoutRoute(Tour tour){

        this.ratingCalculationService.calculateRating(tour);
        this.popularityCalculationService.calculatePopularity(tour);
        this.childFriendlinessCalculationService.calculateChildFriendliness(tour);
    }

    public void generateAiSummary(Tour tour){
        AiSummaryTask aiSummaryTask = new AiSummaryTask(tour, aiSummaryService);

        Thread thread = new Thread(aiSummaryTask);
        thread.setDaemon(true); //abort request if ui is closed
        thread.start();
    }

    public void addTour(Tour tour){

        tour.setIsNew(false);

        tourRepository.persist(tour);

        this.ratingCalculationService.calculateRating(tour);
        this.popularityCalculationService.calculatePopularity(tour);

        //create new task for api request (to run async)
        TourMapRequestTask tourMapRequestTask = new TourMapRequestTask(tour, tourMapService); //create new task

        this.getAllToursList().add(tour);

        Thread thread = new Thread(tourMapRequestTask);
        thread.setDaemon(true); //abort request if ui is closed
        thread.start();

    }

    public void deleteTour(Tour tour){
        if(!tour.isNew()){
            tourRepository.delete(tour);
        }
        deleteFile(tour.getPathToMapImage());
        if(selectedTourProperty.get() == tour){
            selectedTourProperty.set(null);
        }
        allTours.remove(tour);
    }

    public static void deleteFile(String filePath) {

        if (filePath == null) {
            return;
        }

        File file = new File(filePath);

        if (file.exists()) {
            boolean isDeleted = file.delete();

            if (isDeleted) {
                logger.info("Deleted tour image");
            } else {
                logger.error("Could not delete tour image");
            }
        } else {
            logger.warn("Could not delete tour image because it does not exist");
        }
    }

}
