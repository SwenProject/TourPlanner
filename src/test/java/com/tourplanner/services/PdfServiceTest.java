package com.tourplanner.services;

import com.tourplanner.enums.Difficulty;
import com.tourplanner.enums.TransportType;
import com.tourplanner.models.Tour;
import com.tourplanner.models.TourLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PdfServiceTest{
    Tour tour1;
    Tour tour2;
    ArrayList<Tour> tours;
    PdfService pdf_service = new PdfService();

    @BeforeEach
    void setUp() {
        tour1 = new Tour();
        tour1.setName("SuperTour");
        tour1.setStartingPoint("Wien");
        tour1.setDestinationPoint("Graz");
        tour1.setDescription("Eine super TourEine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour");
        tour1.setDistance(300.);
        tour1.setPathToMapImage("./src/test/resources/com/tourplanner/services/testMapImages/testMapImage1.jpeg");
        tour1.setPathToMapImage(null);
        tour1.setDuration(Duration.ofHours(2));
        tour1.setTransportType(TransportType.CAR);

        tour2 = new Tour();
        tour2.setName("NichtsoSuperTour");
        tour2.setStartingPoint("Wien");
        tour2.setDestinationPoint("Linz");
        tour2.setDescription("Eh auch gut");
        tour2.setDistance(200.);
        tour2.setTransportType(TransportType.FEET);
        tour2.setPathToMapImage("./src/test/resources/com/tourplanner/services/testMapImages/testMapImage1.jpeg");
        tour2.setDuration(Duration.ofHours(3));

        TourLog log1 = new TourLog();
        log1.setRating(5);
        log1.setTotalTime(Duration.ofHours(2));
        log1.setDifficulty(Difficulty.MEDIUM);
        log1.setDate(new Date(2023-1900, Calendar.AUGUST, 3, 22, 30, 18));
        log1.setComment("Es war schrecklich!");


        TourLog log2 = new TourLog();
        log2.setRating(4);
        log2.setTotalTime(Duration.ofHours(1));
        log2.setDifficulty(Difficulty.MEDIUM);
        log2.setDate(new Date(2023-1900, Calendar.AUGUST, 3, 22, 30, 18));
        log2.setComment("Hab gehört es soll schrecklich sein aber eigentlich cool!");

        tour1.getTourLogs().add(log1);
        tour1.getTourLogs().add(log2);

        tours = new ArrayList<>();
        tours.add(tour1);
        tours.add(tour2);
    }

    @Test
    @DisplayName("Create PDF summary of multiple tours")
    void testCreatePdfSummary() throws IOException {
        //arrange
        //Create a temporary directory and file path for testing
        Path tempDir = Files.createTempDirectory("tourplanner-pdf-test");
        Path tempFile = tempDir.resolve("test-pdf.pdf");

        try {
            //act
            pdf_service.createPdfSummary(tours, tempFile.toString());

            //assert
            //Check if file exists
            assert Files.exists(tempFile);
        } finally {
            //Delete temporary directory and file
            Files.deleteIfExists(tempFile);
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    @DisplayName("Create PDF summary of multiple tours with a tour that has no image")
    void testCreatePdfSummaryOneTourNoImage() throws IOException {
        //arrange
        Tour testTour = new Tour();
        testTour.setName("SuperTour");
        testTour.setStartingPoint("Wien");
        testTour.setDestinationPoint("Graz");
        testTour.setDescription("Eine super TourEine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour");
        testTour.setDistance(300.);
        testTour.setPathToMapImage("error"); //tour can have "error" as path to image when tour could not be calculated
        testTour.setDuration(Duration.ofHours(2));
        testTour.setTransportType(TransportType.CAR);
        testTour.setTourLogs(new ArrayList<>());

        ArrayList<Tour> testTours = new ArrayList<>();
        testTours.add(testTour);
        testTours.add(tour1);
        testTours.add(tour2);

        //Create a temporary directory and file path for testing
        Path tempDir = Files.createTempDirectory("tourplanner-pdf-test");
        Path tempFile = tempDir.resolve("test-pdf.pdf");

        try {
            //act
            pdf_service.createPdfSummary(testTours, tempFile.toString());

            //assert
            //Check if file exists
            assert Files.exists(tempFile);
        } finally {
            //Delete temporary directory and file
            Files.deleteIfExists(tempFile);
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    @DisplayName("Create PDF of single tour")
    void testCreatePdfSingleTour() throws IOException {
        //arrange
        //Create a temporary directory and file path for testing
        Path tempDir = Files.createTempDirectory("tourplanner-pdf-test");
        Path tempFile = tempDir.resolve("test-pdf.pdf");

        try {
            //act
            pdf_service.createPdfSingleTour(tour1, tempFile.toString());

            //assert
            //Check if file exists
            assert Files.exists(tempFile);
        } finally {
            //Delete temporary directory and file
            Files.deleteIfExists(tempFile);
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    @DisplayName("Create PDF of single tour without logs and map image")
    void testCreatePdfSingleTourNoLogsNoImage() throws IOException {
        //arrange
        Tour testTour = new Tour();
        testTour.setName("SuperTour");
        testTour.setStartingPoint("Wien");
        testTour.setDestinationPoint("Graz");
        testTour.setDescription("Eine super TourEine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour Eine super Tour");
        testTour.setDistance(300.);
        testTour.setPathToMapImage(null);
        testTour.setDuration(Duration.ofHours(2));
        testTour.setTransportType(TransportType.CAR);
        testTour.setTourLogs(new ArrayList<>());

        //Create a temporary directory and file path for testing
        Path tempDir = Files.createTempDirectory("tourplanner-pdf-test");
        Path tempFile = tempDir.resolve("test-pdf.pdf");

        try {
            //act
            pdf_service.createPdfSingleTour(testTour, tempFile.toString());

            //assert
            //Check if file exists
            assert Files.exists(tempFile);
        } finally {
            //Delete temporary directory and file
            Files.deleteIfExists(tempFile);
            Files.deleteIfExists(tempDir);
        }
    }

}