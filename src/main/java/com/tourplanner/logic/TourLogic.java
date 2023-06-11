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

    public void saveSelectedTour(){

        this.ratingCalculationService.calculateRating(selectedTourProperty.get());
        this.popularityCalculationService.calculatePopularity(selectedTourProperty.get());

        //create new task for api request (to run async)
        TourMapRequestTask tourMapRequestTask = new TourMapRequestTask(selectedTourProperty.get(), tourMapService); //create new task

        if(selectedTourProperty.get().isNew()) {
            selectedTourProperty.get().setIsNew(false); //tour is not new anymore
            //set callback of task to db save function
            tourMapRequestTask.setCallback(tourRepository::save);

            allTours.add(selectedTourProperty.get()); //add new tour to all tours list
        } else { //tour is not new and has an id

            //set callback of task to db update function
            tourMapRequestTask.setCallback(tourRepository::update);
        }

        Thread thread = new Thread(tourMapRequestTask);
        thread.setDaemon(true); //abort request if ui is closed
        thread.start();
    }

    public void updateSelectedTourWithoutRecalculating(){

        this.ratingCalculationService.calculateRating(selectedTourProperty.get());
        this.popularityCalculationService.calculatePopularity(selectedTourProperty.get());
        this.childFriendlinessCalculationService.calculateChildFriendliness(selectedTourProperty.get());


        tourRepository.update(selectedTourProperty.get());

    }

    public void generateAiSummaryForSelectedTour(){
        AiSummaryTask aiSummaryTask = new AiSummaryTask(selectedTourProperty.get(), aiSummaryService, tourRepository::update);

        Thread thread = new Thread(aiSummaryTask);
        thread.setDaemon(true); //abort request if ui is closed
        thread.start();
    }

    public void addTour(Tour tour){

        this.ratingCalculationService.calculateRating(tour);
        this.popularityCalculationService.calculatePopularity(tour);


        //create new task for api request (to run async)
        TourMapRequestTask tourMapRequestTask = new TourMapRequestTask(tour, tourMapService); //create new task

        //set callback of task to db save function
        tourMapRequestTask.setCallback(tourRepository::save);

        this.getAllToursList().add(tour);

        Thread thread = new Thread(tourMapRequestTask);
        thread.setDaemon(true); //abort request if ui is closed
        thread.start();

    }

    public void deleteSelectedTour(){

        deleteFile(selectedTourProperty.get().getPathToMapImage());

        if(selectedTourProperty.get().isNew()) { //tour is new and has no id
            selectedTourProperty.set(null);
            //no need to delete the new tour from db or all tours list because it was never saved (has no id)
            return;
        }

        //if the tour is not new, delete it from db and all tours list
        tourRepository.delete(selectedTourProperty.get());
        allTours.remove(selectedTourProperty.get());
        selectedTourProperty.set(null);
    }

    //to delete specific tour e.g. in tour list edit mode
    public void deleteTour(Tour tour){

        deleteFile(tour.getPathToMapImage());

        if(selectedTourProperty.get() == tour){
            selectedTourProperty.set(null);
        }

        tourRepository.delete(tour);
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
