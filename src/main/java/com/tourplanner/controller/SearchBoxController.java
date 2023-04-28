package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

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

    public SearchBoxController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize(){

        //call onSearch() to load all tours when search box is initialized
        onSearch(null);

        //when allTours list changes, redo search
        tourLogic.getAllToursList().addListener(((ListChangeListener<Tour>) c -> onSearch(null)));

        //only show clear search button if search field is not empty
        clearSearchButton.visibleProperty().bind(Bindings.isNotEmpty(searchTextField.textProperty())); //Bind clear search button to search text field

        //only enable clear advanced search button if any of the advanced search fields are not empty
        clearAdvancedSearchButton.disableProperty().bind(Bindings.isEmpty(searchStartLocationField.textProperty())
                .and(Bindings.isEmpty(searchEndLocationField.textProperty()))
                .and(Bindings.greaterThanOrEqual(searchMaxDistanceSlider.valueProperty(), searchMaxDistanceSlider.getMax()))
                .and(Bindings.lessThanOrEqual(searchMinDistanceSlider.valueProperty(), searchMinDistanceSlider.getMin()))
        );

        //bind max/min distance label to slider value
        searchMaxDistanceLabel.textProperty().bind(searchMaxDistanceSlider.valueProperty().asString("%.1f km"));
        searchMinDistanceLabel.textProperty().bind(searchMinDistanceSlider.valueProperty().asString("%.1f km"));

        //set default values for sliders
        searchMinDistanceSlider.setValue(searchMinDistanceSlider.getMin());
        searchMaxDistanceSlider.setValue(searchMaxDistanceSlider.getMax());
    }

    public void onSearch(ActionEvent actionEvent) {
        //TODO: implement actual search

        //for now, just show all tours
        ObservableList<Tour> allTours = FXCollections.observableArrayList();
        allTours.addAll(tourLogic.getAllToursList());
        allTours.sort((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        tourLogic.getSearchedToursList().setAll(allTours);
    }

    public void toggleAdvancedSearch(ActionEvent actionEvent) {
        advancedSearchContainer.setVisible(!advancedSearchContainer.isVisible());
        advancedSearchContainer.setManaged(!advancedSearchContainer.isManaged());
        advancedSearchButton.setText(advancedSearchContainer.isVisible() ? "Hide Advanced Search" : "Show Advanced Search");

        //reset advanced search fields on hide
        if(!advancedSearchContainer.isVisible()){
            onClearAdvancedSearch(actionEvent);
        }

    }

    public void onClearSearch(MouseEvent actionEvent) {
        searchTextField.clear();
    }

    public void onClearAdvancedSearch(ActionEvent actionEvent) {
        searchStartLocationField.clear();
        searchEndLocationField.clear();
        searchMaxDistanceSlider.setValue(searchMaxDistanceSlider.getMax());
        searchMinDistanceSlider.setValue(searchMinDistanceSlider.getMin());
    }
}
