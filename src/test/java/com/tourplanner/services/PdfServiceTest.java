package com.tourplanner.services;

import com.tourplanner.enums.Difficulty;
import com.tourplanner.enums.TransportType;
import com.tourplanner.models.Tour;
import com.tourplanner.models.TourLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

class PdfServiceTest{
    Tour tour1;
    Tour tour2;
    ArrayList<Tour> tours;
    PdfService pdf_service = new PdfService();

    @BeforeEach
    void setUp() {
        tour1 = new Tour();
        tour2 = new Tour();
        tours = new ArrayList<Tour>();
        tours.add(tour1);
        tours.add(tour2);

        TourLog log1 = new TourLog();
        log1.setRating(5);
        log1.setTotalTime(Duration.ofHours(2));
        log1.setDifficulty(Difficulty.MEDIUM);
        log1.setDate(new Date(2023, Calendar.AUGUST, 3, 22, 30, 18));
        log1.setComment("Es war schrecklich!");


        TourLog log2 = new TourLog();
        log2.setRating(4);
        log2.setTotalTime(Duration.ofHours(1));
        log2.setDifficulty(Difficulty.MEDIUM);
        log2.setDate(new Date(2023, Calendar.AUGUST, 3, 22, 30, 18));
        log2.setComment("Hab gehört es soll schrecklich sein aber eigentlich cool!");



        TourLog log3 = new TourLog();
        log3.setRating(3);
        log3.setTotalTime(Duration.ofHours(3));
        TourLog log4 = new TourLog();
        log4.setRating(2);
        log4.setTotalTime(Duration.ofHours(4));

        tour1.getTourLogs().add(log1);
        tour1.getTourLogs().add(log2);
        tour2.getTourLogs().add(log3);
        tour2.getTourLogs().add(log4);

    }

    @Test
    void createPdfSummaryTest() {
        //arrange
        tour1.setName("SuperTour");
        tour1.setStartingPoint("Wien");
        tour1.setDestinationPoint("Graz");
        tour1.setDescription("Eine super TourEine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour");
        tour1.setDistance(300.);
        tour1.setPathToMapImage("./src/main/resources/com/tourplanner/map_images/422586223.jpeg");

        tour1.setTransportType(TransportType.CAR);

        tour2.setName("NichtsoSuperTour");
        tour2.setStartingPoint("Wien");
        tour2.setDestinationPoint("Linz");
        tour2.setDescription("Eh auch gut");
        tour2.setDistance(200.);
        tour2.setTransportType(TransportType.FEET);
        tour2.setPathToMapImage("./src/main/resources/com/tourplanner/map_images/780295808.jpeg");


        //act
        pdf_service.createPdfSummary(tours, "./src/main/resources/com/tourplanner/pdfs/test.pdf");
        //assert
    }

    @Test
    void createPdfSingleTourTest(){
        //arrange
        tour1.setName("SuperTour");
        tour1.setStartingPoint("Wien");
        tour1.setDestinationPoint("Graz");
        tour1.setDescription("Eine super TourEine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour");
        tour1.setDistance(300.);
        tour1.setPathToMapImage("./src/main/resources/com/tourplanner/map_images/422586223.jpeg");

        tour1.setTransportType(TransportType.CAR);

        //act
        pdf_service.createPdfSingleTour(tour1, "./src/main/resources/com/tourplanner/pdfs/testSingleTour.pdf");
        //assert
    }

}