package com.tourplanner.models;
import com.tourplanner.enums.Difficulty;
import javax.persistence.*;
import java.time.Duration;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tourlogs")
public class TourLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    int rating;
    Difficulty difficulty;
    @Column(name = "total_time")
    Duration totalTime;
    String comment;
    Date date;

    //default constructor
    public TourLog() {
    }
}
