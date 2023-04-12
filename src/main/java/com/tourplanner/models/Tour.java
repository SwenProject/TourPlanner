package com.tourplanner.models;
import com.tourplanner.enums.TransportType;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "tours")
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @Column(name = "starting_point")
    String startingPoint;
    @Column(name = "destination_point")
    String destinationPoint;
    String description;
    double distance;
    Duration duration;
    int rating;
    @Column(name = "transport_type")
    TransportType transportType; //enum: car, bike, train, feet
    @Column(name = "path_to_map_image")
    String pathToMapImage;
    @OneToMany(
            cascade = CascadeType.ALL, //All operations on parent are cascaded to children
            orphanRemoval = true, //Children are deleted when they are removed from the collection
            fetch = FetchType.EAGER //Children are fetched with parent
    )
    @JoinColumn(name = "fk_tour_id") //Foreign key in child table
    List<TourLog> tourLogs = new ArrayList<>();

    public Tour(){  //default constructor for ORM
    }

}
