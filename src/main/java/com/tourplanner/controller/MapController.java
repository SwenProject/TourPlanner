package com.tourplanner.controller;

import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
import javafx.beans.property.*;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MapController {
    private final TourLogic tourLogic;

    private final StringProperty currentPathToImage = new SimpleStringProperty();
    private final BooleanProperty imageIsLoading = new SimpleBooleanProperty(false);
    public ImageView mapImage;
    public VBox noMapContainer;
    public VBox mapErrorContainer;
    public ProgressIndicator loadingSpinner;
    public VBox mapContainer;
    public final DoubleProperty zoomFactor = new SimpleDoubleProperty(1);
    public StackPane mapImageContainer;
    public final DoubleProperty imageLoadingProgress = new SimpleDoubleProperty(0);

    public MapController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize(){

        //bind visibility of noMapContainer and mapImage to currentPathToImage property
        noMapContainer.visibleProperty().bind(currentPathToImage.isNull());
        noMapContainer.managedProperty().bind(currentPathToImage.isNull());
        mapImageContainer.visibleProperty().bind(currentPathToImage.isNotNull().and(currentPathToImage.isNotEqualTo("loading").and(currentPathToImage.isNotEqualTo("error"))).and(imageLoadingProgress.isEqualTo(1)));
        mapImageContainer.managedProperty().bind(currentPathToImage.isNotNull().and(currentPathToImage.isNotEqualTo("loading").and(currentPathToImage.isNotEqualTo("error"))).and(imageLoadingProgress.isEqualTo(1)));
        mapErrorContainer.visibleProperty().bind(currentPathToImage.isEqualTo("error").or(currentPathToImage.isEqualTo("loading").and(imageIsLoading.not())));
        mapErrorContainer.managedProperty().bind(currentPathToImage.isEqualTo("error").or(currentPathToImage.isEqualTo("loading").and(imageIsLoading.not())));
        loadingSpinner.visibleProperty().bind(imageIsLoading);
        loadingSpinner.managedProperty().bind(imageIsLoading);

        //call loadTour when current tour changes
        tourLogic.getSelectedTourProperty().addListener((observable, oldValue, newValue) -> loadTour(newValue));

        //bind loadImage to currentPathToImage property
        currentPathToImage.addListener((observable, oldValue, newValue) -> loadImage(newValue));

        resizeImage();

        mapContainer.widthProperty().addListener((observable, oldValue, newValue) -> resizeImage());

        mapContainer.heightProperty().addListener((observable, oldValue, newValue) -> resizeImage());

        zoomFactor.addListener((observable, oldValue, newValue) -> resizeImage());
    }

    public void loadTour(Tour tour){

        if(tour == null){
            currentPathToImage.unbind();
            currentPathToImage.set(null);
            imageIsLoading.unbind();
            imageIsLoading.set(false);
            return;
        }

        currentPathToImage.bind(tour.getPathToMapImageProperty());
        imageIsLoading.bind(tour.getImageIsLoadingProperty());
    }

    private void resizeImage(){

        if(mapContainer.getHeight() > mapContainer.getWidth()) {
            mapImage.fitWidthProperty().unbind();
            mapImage.setFitWidth(-1);
            mapImage.fitHeightProperty().bind(mapContainer.heightProperty().multiply(zoomFactor.get()));
        }else{
            mapImage.fitHeightProperty().unbind();
            mapImage.setFitHeight(-1);
            mapImage.fitWidthProperty().bind(mapContainer.widthProperty().subtract(50).multiply(zoomFactor.get()));
        }
    }

    private void loadImage(String pathToImage){
        if(pathToImage == null || pathToImage.isEmpty() || pathToImage.equals("error") || pathToImage.equals("loading")){
            mapImage.setImage(null);
            return;
        }

        Path path = Paths.get(pathToImage);

        File file = path.toFile();

        Image image = new Image(file.toURI().toString(), true); //true to load image in separate thread

        imageLoadingProgress.bind(image.progressProperty());

        mapImage.setImage(image);
    }

    public void onIncreaseZoom(){
        zoomFactor.set(zoomFactor.get() * 1.4);
    }

    public void onDecreaseZoom(){
        if(zoomFactor.get() > 1){
            zoomFactor.set(zoomFactor.get() / 1.4);
        }
    }

}
