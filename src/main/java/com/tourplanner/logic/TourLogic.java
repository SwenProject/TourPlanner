package com.tourplanner.logic;

import com.tourplanner.enums.TransportType;
import com.tourplanner.models.Tour;
import com.tourplanner.repositories.TourRepository;
import com.tourplanner.services.ITourMapService;
import com.tourplanner.services.PopularityCalculationService;
import com.tourplanner.services.RatingCalculationService;
import com.tourplanner.services.TourMapRequestTask;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

import java.util.concurrent.Semaphore;

public class TourLogic {
    private final TourRepository tourRepository;
    private final ITourMapService tourMapService;
    private final Semaphore tourMapRequestLock = new Semaphore(1); //semaphore to ensure that only one api request is made at a time
    private final ObservableList<Tour> allTours = FXCollections.observableArrayList();
    private final FilteredList<Tour> searchedTours = new FilteredList<>(allTours);
    private final ListProperty<Tour> searchedToursListProperty = new SimpleListProperty<>(searchedTours);
    private final ObjectProperty<Tour> selectedTourProperty = new SimpleObjectProperty<>();
    private final IntegerProperty currentTabProperty = new SimpleIntegerProperty(0);
    private final RatingCalculationService ratingCalculationService = new RatingCalculationService();
    private final PopularityCalculationService popularityCalculationService = new PopularityCalculationService();

    public TourLogic(TourRepository tourRepository, ITourMapService tourMapService) {
        this.tourRepository = tourRepository;
        this.tourMapService = tourMapService;

        allTours.setAll(tourRepository.getAll()); //load all tours from db in constructor
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

        Tour newTour = new Tour();

        //set default selected Transport Type
        newTour.setTransportType(TransportType.CAR);

        //mark tour as new
        newTour.setIsNew(true);

        selectedTourProperty.set(newTour);
    }

    public void saveSelectedTour(){

        //TODO:
        // - child friendliness calculation
        // - popularity calculation

        this.ratingCalculationService.calculateRating(selectedTourProperty.get());
        this.popularityCalculationService.calculatePopularity(selectedTourProperty.get());

        //create new task for api request (to run async)
        TourMapRequestTask tourMapRequestTask = new TourMapRequestTask(selectedTourProperty.get(), tourMapService, tourMapRequestLock); //create new task

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

        //TODO:
        // - child friendliness calculation
        // - popularity calculation

        this.ratingCalculationService.calculateRating(selectedTourProperty.get());
        this.popularityCalculationService.calculatePopularity(selectedTourProperty.get());

        tourRepository.update(selectedTourProperty.get());

    }

    public void addTour(Tour tour){

        //TODO:
        // - child friendliness calculation
        // - popularity calculation

        this.ratingCalculationService.calculateRating(tour);
        this.popularityCalculationService.calculatePopularity(tour);

        //create new task for api request (to run async)
        TourMapRequestTask tourMapRequestTask = new TourMapRequestTask(tour, tourMapService, tourMapRequestLock); //create new task

        //set callback of task to db save function
        tourMapRequestTask.setCallback(tourRepository::save);

        Thread thread = new Thread(tourMapRequestTask);
        thread.setDaemon(true); //abort request if ui is closed
        thread.start();

        this.getAllToursList().add(tour);
    }

    public void deleteSelectedTour(){

        //TODO delete map image from file system

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

    //TODO: delete map image from file system
    //to delete specific tour e.g. in tour list edit mode
    public void deleteTour(Tour tour){
        if(selectedTourProperty.get() == tour){
            selectedTourProperty.set(null);
        }

        tourRepository.delete(tour);
        allTours.remove(tour);
    }

}
