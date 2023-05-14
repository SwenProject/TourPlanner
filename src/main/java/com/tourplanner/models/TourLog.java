package com.tourplanner.models;
import com.tourplanner.enums.Difficulty;
import javax.persistence.*;
import java.time.Duration;
import java.util.Date;
import javafx.beans.property.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tourlogs")
@Access(AccessType.PROPERTY)
public class TourLog {

    // ------- TourLog Properties -------

    private final LongProperty idProperty = new SimpleLongProperty();
    private final IntegerProperty ratingProperty = new SimpleIntegerProperty();
    private final ObjectProperty<Difficulty> difficultyProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Duration> totalTimeProperty = new SimpleObjectProperty<>();
    private final StringProperty commentProperty = new SimpleStringProperty();
    private final ObjectProperty<Date> dateProperty = new SimpleObjectProperty<>();

    // ------- Getter for TourLog Properties -------
    // annotation @Transient means that this property is not mapped to the database

    @Transient
    public LongProperty getIdProperty() {
        return idProperty;
    }

    @Transient
    public IntegerProperty getRatingProperty() {
        return ratingProperty;
    }

    @Transient
    public ObjectProperty<Difficulty> getDifficultyProperty() {
        return difficultyProperty;
    }

    @Transient
    public ObjectProperty<Duration> getTotalTimeProperty() {
        return totalTimeProperty;
    }

    @Transient
    public StringProperty getCommentProperty() {
        return commentProperty;
    }

    @Transient
    public ObjectProperty<Date> getDateProperty() {
        return dateProperty;
    }

    // ------- Getter and Setter for actual data so that the Tour can be saved to db -------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return idProperty.get();
    }
    public void setId(Long id) {
        this.idProperty.set(id);
    }

    public int getRating() {
        return ratingProperty.get();
    }
    public void setRating(int rating) {
        this.ratingProperty.set(rating);
    }

    public Difficulty getDifficulty() {
        return difficultyProperty.get();
    }
    public void setDifficulty(Difficulty difficulty) {
        this.difficultyProperty.set(difficulty);
    }

    @Column(name = "total_time")
    public Duration getTotalTime() {
        return totalTimeProperty.get();
    }
    public void setTotalTime(Duration totalTime) {
        this.totalTimeProperty.set(totalTime);
    }

    public String getComment() {
        return commentProperty.get();
    }
    public void setComment(String comment) {
        this.commentProperty.set(comment);
    }

    public Date getDate() {
        return dateProperty.get();
    }
    public void setDate(Date date) {
        this.dateProperty.set(date);
    }

    //default constructor
    public TourLog() {
    }
}
