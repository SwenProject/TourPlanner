package com.tourplanner.logic;

import com.tourplanner.enums.TransportType;
import com.tourplanner.models.Tour;
import com.tourplanner.repositories.TourRepository;
import com.tourplanner.services.ITourMapService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TourLogic {
    private final TourRepository tourRepository = new TourRepository();
    private final ObservableList<Tour> allTours = FXCollections.observableArrayList();
    private final ObservableList<Tour> searchedTours = FXCollections.observableArrayList();
    private final ObjectProperty<Tour> selectedTour = new SimpleObjectProperty<>();

    public ObservableList<Tour> getAllToursList() {
        return allTours;
    }

    public ObservableList<Tour> getSearchedToursList() {
        return searchedTours;
    }

    public ObjectProperty<Tour> getSelectedTourProperty() {
        return selectedTour;
    }

    public TourLogic(){ //load all tours from db in constructor
        allTours.setAll(tourRepository.getAll());
    }

    public void setSelectedTour(Tour tour) {
        selectedTour.set(tour);
    }

    public void createNewTour(){

        Tour newTour = new Tour();

        //set default selected Transport Type
        newTour.setTransportType(TransportType.CAR);

        selectedTour.set(newTour);
    }

    public void saveSelectedTour(){

        //TODO:
        // - rating calculation
        // - use TourMapService -> inject in constr of TourLogic


        ITourMapService tourMapService = new TourMapServiceMapQuest();
        tourMapService.calculateRoute(selectedTour.get());

        if(selectedTour.get().getId() == null) { //tour is new and has no id
            tourRepository.save(selectedTour.get()); //save new tour to db
            allTours.add(selectedTour.get()); //add new tour to all tours list

            //reset selected tour to reload map and tour info
            Tour tempTour = selectedTour.get();
            selectedTour.set(null);
            selectedTour.set(tempTour);

            return;
        }

        //update existing tour
        tourRepository.update(selectedTour.get());
        //tour in all tours list is already updated because it is a reference to the selected tour

        //reset selected tour to reload map and tour info
        Tour tempTour = selectedTour.get();
        selectedTour.set(null);
        selectedTour.set(tempTour);

    }

    public void deleteSelectedTour(){

        //TODO delete map image from file system

        if(selectedTour.get().getId() == null) { //tour is new and has no id
            selectedTour.set(null);
            //no need to delete the new tour from db or all tours list because it was never saved (has no id)
            return;
        }

        //if the tour is not new, delete it from db and all tours list
        tourRepository.delete(selectedTour.get());
        allTours.remove(selectedTour.get());
        selectedTour.set(null);
    }

    //TODO: delete map image from file system
    //to delete specific tour e.g. in tour list edit mode
    public void deleteTour(Tour tour){
        if(selectedTour.get() == tour){
            selectedTour.set(null);
        }

        tourRepository.delete(tour);
        allTours.remove(tour);
    }

}
