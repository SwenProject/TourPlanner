package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SearchBoxController {
    private final TourLogic tourLogic;
    public VBox advancedSearchContainer;
    public Button advancedSearchButton;
    public TextField searchTextField;
    public Region clearSearchButton;
    public Button clearAdvancedSearchButton;
    public TextField searchStartLocationField;
    public TextField searchEndLocationField;
    public Slider searchMaxDistanceSlider;
    public Slider searchMinDistanceSlider;
    public Label searchMaxDistanceLabel;
    public Label searchMinDistanceLabel;

    private final BooleanProperty advancedSearchActive = new SimpleBooleanProperty(false);

    private static final Logger logger = LogManager.getLogger(SearchBoxController.class);

    public SearchBoxController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize(){

        recalculateMinMaxDistance();

        //when allTours list changes, redo search
        tourLogic.getAllToursList().addListener(((ListChangeListener<Tour>) c -> onSearch()));

        //only show clear search button if search field is not empty
        clearSearchButton.visibleProperty().bind(Bindings.isNotEmpty(searchTextField.textProperty())); //Bind clear search button to search text field

        advancedSearchActive.bind(Bindings.isEmpty(searchStartLocationField.textProperty())
                .and(Bindings.isEmpty(searchEndLocationField.textProperty()))
                .and(Bindings.greaterThanOrEqual(searchMaxDistanceSlider.valueProperty(), searchMaxDistanceSlider.maxProperty()))
                .and(Bindings.lessThanOrEqual(searchMinDistanceSlider.valueProperty(), searchMinDistanceSlider.minProperty()))
                .not()
        );

        //only enable clear advanced search button if any of the advanced search fields are not empty
        clearAdvancedSearchButton.disableProperty().bind(advancedSearchActive.not());

        //bind max/min distance label to slider value
        searchMaxDistanceLabel.textProperty().bind(searchMaxDistanceSlider.valueProperty().asString("%.1f km"));
        searchMinDistanceLabel.textProperty().bind(searchMinDistanceSlider.valueProperty().asString("%.1f km"));

        //set default values for sliders
        searchMinDistanceSlider.setValue(searchMinDistanceSlider.getMin());
        searchMaxDistanceSlider.setValue(searchMaxDistanceSlider.getMax());

        //call onSearch when search text field changes
        searchTextField.textProperty().addListener((observable, oldValue, newValue) -> onSearch());

        //call onSearch when advanced search fields change
        searchStartLocationField.textProperty().addListener((observable, oldValue, newValue) -> onSearch());
        searchEndLocationField.textProperty().addListener((observable, oldValue, newValue) -> onSearch());
        searchMaxDistanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> onSearch());
        searchMinDistanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> onSearch());
    }

    public void onSearch() {
        //reset predicate to redo search in filteredList
        tourLogic.getSearchedToursList().setPredicate(this::matchesSearch);
    }

    public boolean matchesSearch(Tour tour){
        //TODO: implement actual search

        String[] searchedWords = searchTextField.getText().split(" ");

        for (String word : searchedWords){
            if(tour.getName() == null || tour.getName().toLowerCase().replace(" ", "").contains(word.toLowerCase())){
                if(advancedSearchActive.get()){
                    return advancedMatchesSearch(tour);
                }
                return true;
            }
        }

        return false;
    }

    private boolean advancedMatchesSearch(Tour tour){
        String[] searchedStartingPoint = searchStartLocationField.getText().split(" ");

        boolean match = false;
        for (String word : searchedStartingPoint){
            if(tour.getStartingPoint() == null || tour.getStartingPoint().toLowerCase().replace(" ", "").contains(word.toLowerCase())){
                match = true;
            }
        }
        if(!match){
            return false;
        }

        String[] searchedDestination = searchEndLocationField.getText().split(" ");

        match = false;
        for (String word : searchedDestination){
            if(tour.getDestinationPoint() == null || tour.getDestinationPoint().toLowerCase().replace(" ", "").contains(word.toLowerCase())){
                match = true;
            }
        }
        if(!match){
            return false;
        }

        return tour.getDistance() >= searchMinDistanceSlider.getValue() && tour.getDistance() <= searchMaxDistanceSlider.getValue();

    }

    public void toggleAdvancedSearch() {

        if(!advancedSearchContainer.isVisible()){
            recalculateMinMaxDistance();
        }

        advancedSearchContainer.setVisible(!advancedSearchContainer.isVisible());
        advancedSearchContainer.setManaged(!advancedSearchContainer.isManaged());
        advancedSearchButton.setText(advancedSearchContainer.isVisible() ? "Hide Advanced Search" : "Show Advanced Search");

        //reset advanced search fields on hide
        if(!advancedSearchContainer.isVisible()){
            onClearAdvancedSearch();
        }
    }

    public void onClearSearch() {
        logger.debug("Cleared search");
        searchTextField.textProperty().setValue("");
    }

    public void onClearAdvancedSearch() {
        logger.debug("Cleared advanced search");
        searchStartLocationField.textProperty().setValue("");
        searchEndLocationField.textProperty().setValue("");
        searchMaxDistanceSlider.valueProperty().setValue(searchMaxDistanceSlider.getMax());
        searchMinDistanceSlider.valueProperty().setValue(searchMinDistanceSlider.getMin());
    }

    public void recalculateMinMaxDistance(){
        double minDistance = tourLogic.getAllToursList().stream().mapToDouble(Tour::getDistance).min().orElse(0);
        double maxDistance = tourLogic.getAllToursList().stream().mapToDouble(Tour::getDistance).max().orElse(0);

        if(minDistance < 0){
            minDistance = 0;
        }

        if(maxDistance < 0){
            maxDistance = 0;
        }

        searchMinDistanceSlider.setMin(minDistance);
        searchMinDistanceSlider.setMax(maxDistance);
        searchMaxDistanceSlider.setMin(minDistance);
        searchMaxDistanceSlider.setMax(maxDistance);

        searchMinDistanceSlider.setValue(minDistance);
        searchMaxDistanceSlider.setValue(maxDistance);
    }
}
