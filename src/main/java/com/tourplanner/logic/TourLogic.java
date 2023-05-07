package com.tourplanner.logic;

import com.tourplanner.enums.TransportType;
import com.tourplanner.models.Tour;
import com.tourplanner.repositories.TourRepository;
import com.tourplanner.services.ITourMapService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class TourLogic {
    private final TourRepository tourRepository = new TourRepository();
    private final ObservableList<Tour> allTours = FXCollections.observableArrayList();
    private final FilteredList<Tour> searchedTours = new FilteredList<>(allTours);
    private final ObjectProperty<Tour> selectedTourProperty = new SimpleObjectProperty<>();

    public TourLogic(){ //load all tours from db in constructor
        allTours.setAll(tourRepository.getAll());
    }

    public ObservableList<Tour> getAllToursList() {
        return allTours;
    }

    public FilteredList<Tour> getSearchedToursList() {
        return searchedTours;
    }

    public ObjectProperty<Tour> getSelectedTourProperty() {
        return selectedTourProperty;
    }

    public void selectTour(Tour tour){
        selectedTourProperty.set(tour);
    }

    public void createNewTour(){

        Tour newTour = new Tour();

        //set default selected Transport Type
        newTour.setTransportType(TransportType.CAR);

        selectedTourProperty.set(newTour);
    }

    public void saveSelectedTour(){

        //TODO:
        // - rating calculation
        // - use TourMapService -> inject in constr of TourLogic


        ITourMapService tourMapService = new TourMapServiceMapQuest();
        tourMapService.calculateRoute(selectedTourProperty.get());

        if(selectedTourProperty.get().getId() == 0) { //tour is new and has no id
            tourRepository.save(selectedTourProperty.get()); //save new tour to db
            allTours.add(selectedTourProperty.get()); //add new tour to all tours list

            //reset selected tour to reload map and tour info
            Tour tempTour = selectedTourProperty.get();
            selectedTourProperty.set(null);
            selectedTourProperty.set(tempTour);

            return;
        }

        //update existing tour
        tourRepository.update(selectedTourProperty.get());
        //tour in all tours list is already updated because it is a reference to the selected tour

    }

    public void deleteSelectedTour(){

        //TODO delete map image from file system

        if(selectedTourProperty.get().getId() == 0) { //tour is new and has no id
            selectedTourProperty.set(null);
            //no need to delete the new tour from db or all tours list because it was never saved (has no id)
            return;
        }

        //if the tour is not new, delete it from db and all tours list
        tourRepository.delete(selectedTourProperty.get());
        allTours.remove(selectedTourProperty.get());
        selectedTourProperty.set(null);
    }

    public void reloadSelectedTourFromDB(){
        tourRepository.reloadFromDB(selectedTourProperty.get());
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
