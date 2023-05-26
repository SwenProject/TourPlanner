package com.tourplanner.controller;

import com.tourplanner.TourPlannerApp;
import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MapController {
    private final TourLogic tourLogic;

    private final StringProperty currentPathToImage = new SimpleStringProperty();
    public ImageView mapImage;
    public VBox noMapContainer;
    public VBox mapErrorContainer;
    public ProgressIndicator loadingSpinner;
    public VBox mapContainer;

    public MapController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize(){

        //bind visibility of noMapContainer and mapImage to currentPathToImage property
        noMapContainer.visibleProperty().bind(currentPathToImage.isNull());
        noMapContainer.managedProperty().bind(currentPathToImage.isNull());
        mapImage.visibleProperty().bind(currentPathToImage.isNotNull().and(currentPathToImage.isNotEqualTo("loading").and(currentPathToImage.isNotEqualTo("error"))));
        mapImage.managedProperty().bind(currentPathToImage.isNotNull().and(currentPathToImage.isNotEqualTo("loading").and(currentPathToImage.isNotEqualTo("error"))));
        mapErrorContainer.visibleProperty().bind(currentPathToImage.isEqualTo("error"));
        mapErrorContainer.managedProperty().bind(currentPathToImage.isEqualTo("error"));
        loadingSpinner.visibleProperty().bind(currentPathToImage.isEqualTo("loading"));
        loadingSpinner.managedProperty().bind(currentPathToImage.isEqualTo("loading"));

        //call loadTour when current tour changes
        tourLogic.getSelectedTourProperty().addListener((observable, oldValue, newValue) -> loadTour(newValue));

        //bind loadImage to currentPathToImage property
        currentPathToImage.addListener((observable, oldValue, newValue) -> loadImage(newValue));

        if(mapContainer.getHeight() > mapContainer.getWidth()) {
            mapImage.fitWidthProperty().unbind();
            mapImage.setFitWidth(3840);
            mapImage.fitHeightProperty().bind(mapContainer.heightProperty());
        }else{
            mapImage.fitHeightProperty().unbind();
            mapImage.setFitHeight(3840);
            mapImage.fitWidthProperty().bind(mapContainer.widthProperty().subtract(42));
        }

        mapContainer.widthProperty().addListener((observable, oldValue, newValue) -> {
            if(mapContainer.getHeight() > mapContainer.getWidth()) {
                mapImage.fitWidthProperty().unbind();
                mapImage.setFitWidth(3840);
                mapImage.fitHeightProperty().bind(mapContainer.heightProperty());
            }else{
                mapImage.fitHeightProperty().unbind();
                mapImage.setFitHeight(3840);
                mapImage.fitWidthProperty().bind(mapContainer.widthProperty().subtract(42));
            }
        });

        mapContainer.heightProperty().addListener((observable, oldValue, newValue) -> {
            if(mapContainer.getHeight() > mapContainer.getWidth()) {
                mapImage.fitWidthProperty().unbind();
                mapImage.setFitWidth(-1);
                mapImage.fitHeightProperty().bind(mapContainer.heightProperty());
            }else{
                mapImage.fitHeightProperty().unbind();
                mapImage.setFitHeight(-1);
                mapImage.fitWidthProperty().bind(mapContainer.widthProperty().subtract(42));
            }
        });
    }

    public void loadTour(Tour tour){

        if(tour == null){
            currentPathToImage.unbind();
            currentPathToImage.set(null);
            return;
        }

        currentPathToImage.bind(tour.getPathToMapImageProperty());
    }


    private void loadImage(String pathToImage){
        if(pathToImage == null || pathToImage.isEmpty() || pathToImage.equals("error") || pathToImage.equals("loading")){
            mapImage.setImage(null);
            return;
        }

        Path path = Paths.get(pathToImage);

        File file = path.toFile();

        System.out.println("File exists: " + file.exists());
        System.out.println("File path: " + file.getAbsolutePath());
        System.out.println("File uri: " + file.toURI());

        Image image = new Image(file.toURI().toString(), true); //true to load image in separate thread

        mapImage.setImage(image);
    }

}
