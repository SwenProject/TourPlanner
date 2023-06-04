package com.tourplanner.models;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tourplanner.enums.TransportType;
import jakarta.persistence.*;

import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tours")
@Access(AccessType.PROPERTY)
public class Tour {

    // ------- Tour Properties -------
    @JsonIgnore
    private final LongProperty idProperty = new SimpleLongProperty();
    @JsonIgnore
    private final StringProperty nameProperty = new SimpleStringProperty();
    @JsonIgnore
    private final StringProperty startingPointProperty = new SimpleStringProperty();
    @JsonIgnore
    private final StringProperty destinationPointProperty = new SimpleStringProperty();
    @JsonIgnore
    private final StringProperty descriptionProperty = new SimpleStringProperty();
    @JsonIgnore
    private final DoubleProperty distanceProperty = new SimpleDoubleProperty();
    @JsonIgnore
    private final ObjectProperty<Duration> durationProperty = new SimpleObjectProperty<>();
    @JsonIgnore
    private final IntegerProperty ratingProperty = new SimpleIntegerProperty();
    @JsonIgnore
    private final ObjectProperty<TransportType> transportTypeProperty = new SimpleObjectProperty<>();
    @JsonIgnore
    private final StringProperty pathToMapImageProperty = new SimpleStringProperty();
    @JsonIgnore
    private ListProperty<TourLog> tourLogsProperty = new SimpleListProperty<>();
    @JsonIgnore
    private final IntegerProperty childFriendlinessScoreProperty = new SimpleIntegerProperty();
    @JsonIgnore
    private final IntegerProperty popularityScoreProperty = new SimpleIntegerProperty();

    // ------- Field for actual list (hibernates needs this as a reference) -------
    @JsonIgnore
    private List<TourLog> tourLogs = new ArrayList<>();

    // ------- Getter for Tour Properties -------
    // annotation @Transient means that this property is not mapped to the database

    @Transient
    public LongProperty getIdProperty() {
        return idProperty;
    }

    @Transient
    public StringProperty getNameProperty() {
        return nameProperty;
    }

    @Transient
    public StringProperty getStartingPointProperty() {
        return startingPointProperty;
    }

    @Transient
    public StringProperty getDestinationPointProperty() {
        return destinationPointProperty;
    }

    @Transient
    public StringProperty getDescriptionProperty() {
        return descriptionProperty;
    }

    @Transient
    public DoubleProperty getDistanceProperty() {
        return distanceProperty;
    }

    @Transient
    public ObjectProperty<Duration> getDurationProperty() {
        return durationProperty;
    }

    @Transient
    public IntegerProperty getRatingProperty() {
        return ratingProperty;
    }

    @Transient
    public ObjectProperty<TransportType> getTransportTypeProperty() {
        return transportTypeProperty;
    }

    @Transient
    public StringProperty getPathToMapImageProperty() {
        return pathToMapImageProperty;
    }

    @Transient
    public ListProperty<TourLog> getTourLogsProperty() {
        return tourLogsProperty;
    }

    @Transient
    public IntegerProperty getChildFriendlinessScoreProperty() {
        return childFriendlinessScoreProperty;
    }

    @Transient
    public IntegerProperty getPopularityScoreProperty() {
        return popularityScoreProperty;
    }

    // ------- Getter and Setter for actual data so that the Tour can be saved to db -------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    public Long getId() {
        return this.idProperty.get();
    }
    public void setId(Long id) {
        this.idProperty.set(id);
    }

    public String getName() {
        return nameProperty.get();
    }
    public void setName(String name) {
        this.nameProperty.set(name);
    }

    @Column(name = "starting_point")
    public String getStartingPoint() {
        return startingPointProperty.get();
    }
    public void setStartingPoint(String startingPoint) {
        this.startingPointProperty.set(startingPoint);
    }

    @Column(name = "destination_point")
    public String getDestinationPoint() {
        return destinationPointProperty.get();
    }
    public void setDestinationPoint(String destinationPoint) {
        this.destinationPointProperty.set(destinationPoint);
    }

    @Column(columnDefinition = "TEXT")
    public String getDescription() {
        return descriptionProperty.get();
    }
    public void setDescription(String description) {
        this.descriptionProperty.set(description);
    }

    @JsonIgnore
    public double getDistance() {
        return distanceProperty.get();
    }
    public void setDistance(double distance) {
        this.distanceProperty.set(distance);
    }

    @JsonIgnore
    public Duration getDuration() {
        return durationProperty.get();
    }
    public void setDuration(Duration duration) {
        this.durationProperty.set(duration);
    }

    @JsonIgnore
    public int getRating() {
        return ratingProperty.get();
    }
    public void setRating(int rating) {
        this.ratingProperty.set(rating);
    }

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "transport_type")
    public TransportType getTransportType() {
        return transportTypeProperty.get();
    }
    public void setTransportType(TransportType transportType) {
        this.transportTypeProperty.set(transportType);
    }

    @Column(name = "path_to_map_image")
    @JsonIgnore
    public String getPathToMapImage() {
        return pathToMapImageProperty.get();
    }
    public void setPathToMapImage(String pathToMapImage) {
        this.pathToMapImageProperty.set(pathToMapImage);
    }

    @OneToMany(
            cascade = CascadeType.ALL, //All operations on parent are cascaded to children
            orphanRemoval = true, //Children are deleted when they are removed from the collection
            fetch = FetchType.EAGER //Children are fetched with parent
    )
    @JoinColumn(name = "fk_tour_id") //Foreign key in child table
    @JsonGetter("tourLogs")
    public List<TourLog> getTourLogs() {
        return this.tourLogs;
    }

    public void setTourLogs(List<TourLog> tourLogs) {
        this.tourLogs = tourLogs;
        this.tourLogsProperty.set(FXCollections.observableList(tourLogs));
    }

    @Column(name = "child_friendliness_score")
    @JsonIgnore
    public int getChildFriendlinessScore() {
        return childFriendlinessScoreProperty.get();
    }

    public void setChildFriendlinessScore(int childFriendlinessScore) {
        this.childFriendlinessScoreProperty.set(childFriendlinessScore);
    }

    @Column(name = "popularity_score")
    @JsonIgnore
    public int getPopularityScore() {
        return popularityScoreProperty.get();
    }

    public void setPopularityScore(int popularityScore) {
        this.popularityScoreProperty.set(popularityScore);
    }

    // ------- New Tour Boolean (not saved to db and only used to mark tours that were not yet saved to db)-------

    @Transient
    @JsonIgnore
    private boolean isNew = false;

    @Transient
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Transient
    @JsonIgnore
    public boolean isNew() {
        return this.isNew;
    }

    // ------- Default constructor for ORM -------

    public Tour(){
    }

}
