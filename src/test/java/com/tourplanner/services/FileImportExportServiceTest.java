package com.tourplanner.services;

import com.tourplanner.enums.Difficulty;
import com.tourplanner.enums.TransportType;
import com.tourplanner.logic.TourLogic;
import com.tourplanner.models.Tour;
import com.tourplanner.models.TourLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

class FileImportExportServiceTest {

    @Test
    @DisplayName("Test exporting and then importing a single tour without logs")
    void testExportImportSingleTour() throws IOException {
        //Arrange
        TourLogic tourLogic = mock(TourLogic.class);
        FileImportExportService fileImportExportService = new FileImportExportService(tourLogic);

            ObservableList<Tour> testTour = createTestTour();

            when(tourLogic.getAllToursList()).thenReturn(testTour);

            //Create a temporary directory and file path for testing
            Path tempDir = Files.createTempDirectory("tourplanner-test");
            Path tempFile = tempDir.resolve("test-tours.json");

        try {

            //captor for the tour that is imported again
            ArgumentCaptor<Tour> tourCaptor = ArgumentCaptor.forClass(Tour.class);

            //Act
            fileImportExportService.exportTours(tempFile.toString()); //export the test tour to the temp file
            fileImportExportService.importTours(tempFile.toString()); //import the test tour from the temp file

            //Assert
            verify(tourLogic, times(1)).getAllToursList();
            verify(tourLogic, times(1)).addTour(tourCaptor.capture());

            Tour importedTour = tourCaptor.getValue();

            //check if the imported tour is the same as the exported tour
            //however some values (like e.g. duration) are not exported/imported because they are recalculated when the tour is imported
            //the id is also not exported/imported because it is generated when the tour is added to the database
            assert importedTour.getName().equals(testTour.get(0).getName());
            assert importedTour.getStartingPoint().equals(testTour.get(0).getStartingPoint());
            assert importedTour.getDestinationPoint().equals(testTour.get(0).getDestinationPoint());
            assert importedTour.getTransportType().equals(testTour.get(0).getTransportType());
            assert importedTour.getTourLogs().size() == testTour.get(0).getTourLogs().size();

        } finally {
            // Clean up the temporary directory and file
            if (Files.exists(tempFile)) {
                Files.delete(tempFile);
            }
            if (Files.exists(tempDir)) {
                Files.delete(tempDir);
            }
        }
    }

    @Test
    @DisplayName("Test exporting and then importing a single tour with logs")
    void testExportImportSingleTourWithLogs() throws IOException {
        //Arrange
        TourLogic tourLogic = mock(TourLogic.class);
        FileImportExportService fileImportExportService = new FileImportExportService(tourLogic);

        ObservableList<Tour> testTour = createTestTour();

        //add a tour log to the test tour

        testTour.get(0).getTourLogs().add(createTestTourLog(1));

        when(tourLogic.getAllToursList()).thenReturn(testTour);

        //Create a temporary directory and file path for testing
        Path tempDir = Files.createTempDirectory("tourplanner-test");
        Path tempFile = tempDir.resolve("test-tours.json");

        try {

            //captor for the tour that is imported again
            ArgumentCaptor<Tour> tourCaptor = ArgumentCaptor.forClass(Tour.class);

            //Act
            fileImportExportService.exportTours(tempFile.toString()); //export the test tour to the temp file
            fileImportExportService.importTours(tempFile.toString()); //import the test tour from the temp file

            //Assert
            verify(tourLogic, times(1)).getAllToursList();
            verify(tourLogic, times(1)).addTour(tourCaptor.capture());

            Tour importedTour = tourCaptor.getValue();

            //check if the imported tour is the same as the exported tour
            //however some values (like e.g. duration) are not exported/imported because they are recalculated when the tour is imported
            //the id is also not exported/imported because it is generated when the tour is added to the database
            assert importedTour.getName().equals(testTour.get(0).getName());
            assert importedTour.getStartingPoint().equals(testTour.get(0).getStartingPoint());
            assert importedTour.getDestinationPoint().equals(testTour.get(0).getDestinationPoint());
            assert importedTour.getTransportType().equals(testTour.get(0).getTransportType());
            assert importedTour.getTourLogs().size() == testTour.get(0).getTourLogs().size();

            //here we also check if the tour log is the same
            assert importedTour.getTourLogs().get(0).getDate().equals(testTour.get(0).getTourLogs().get(0).getDate());
            assert importedTour.getTourLogs().get(0).getTotalTime().equals(testTour.get(0).getTourLogs().get(0).getTotalTime());
            assert importedTour.getTourLogs().get(0).getRating() == testTour.get(0).getTourLogs().get(0).getRating();
            assert importedTour.getTourLogs().get(0).getComment().equals(testTour.get(0).getTourLogs().get(0).getComment());
            assert importedTour.getTourLogs().get(0).getDifficulty().equals(testTour.get(0).getTourLogs().get(0).getDifficulty());

        } finally {
            // Clean up the temporary directory and file
            if (Files.exists(tempFile)) {
                Files.delete(tempFile);
            }
            if (Files.exists(tempDir)) {
                Files.delete(tempDir);
            }
        }
    }

    @Test
    @DisplayName("Test exporting and then importing a multiple tours")
    void testExportImportMultipleTours() throws IOException {
        //Arrange
        TourLogic tourLogic = mock(TourLogic.class);
        FileImportExportService fileImportExportService = new FileImportExportService(tourLogic);

        ObservableList<Tour> testTours = createTestTours();

        when(tourLogic.getAllToursList()).thenReturn(testTours);

        //Create a temporary directory and file path for testing
        Path tempDir = Files.createTempDirectory("tourplanner-test");
        Path tempFile = tempDir.resolve("test-tours.json");

        try {

            //captor for the tour that is imported again
            ArgumentCaptor<Tour> tourCaptor = ArgumentCaptor.forClass(Tour.class);

            //Act
            fileImportExportService.exportTours(tempFile.toString()); //export the test tour to the temp file
            fileImportExportService.importTours(tempFile.toString()); //import the test tour from the temp file

            //Assert
            verify(tourLogic, times(1)).getAllToursList();
            verify(tourLogic, times(testTours.size())).addTour(tourCaptor.capture());

            List<Tour> capturedTours = tourCaptor.getAllValues();

            //check if the imported tours are the same as the exported tours
            //however some values (like e.g. duration) are not exported/imported because they are recalculated when the tour is imported
            //the id is also not exported/imported because it is generated when the tour is added to the database
            assert capturedTours.get(0).getName().equals(testTours.get(0).getName());
            assert capturedTours.get(0).getStartingPoint().equals(testTours.get(0).getStartingPoint());
            assert capturedTours.get(0).getDestinationPoint().equals(testTours.get(0).getDestinationPoint());

            assert capturedTours.get(1).getName().equals(testTours.get(1).getName());
            assert capturedTours.get(1).getStartingPoint().equals(testTours.get(1).getStartingPoint());
            assert capturedTours.get(1).getDestinationPoint().equals(testTours.get(1).getDestinationPoint());

            assert capturedTours.get(2).getName().equals(testTours.get(2).getName());
            assert capturedTours.get(2).getStartingPoint().equals(testTours.get(2).getStartingPoint());
            assert capturedTours.get(2).getDestinationPoint().equals(testTours.get(2).getDestinationPoint());

        } finally {
            // Clean up the temporary directory and file
            if (Files.exists(tempFile)) {
                Files.delete(tempFile);
            }
            if (Files.exists(tempDir)) {
                Files.delete(tempDir);
            }
        }
    }

    @Test
    @DisplayName("Test exporting and then importing multiple tours with tour logs")
    void testExportImportMultipleToursWithTourLogs() throws IOException {
        //Arrange
        TourLogic tourLogic = mock(TourLogic.class);
        FileImportExportService fileImportExportService = new FileImportExportService(tourLogic);

        ObservableList<Tour> testTours = createTestTours();

        //add tour logs to the test tours
        testTours.get(0).getTourLogs().add(createTestTourLog(1));
        testTours.get(1).getTourLogs().add(createTestTourLog(2));
        testTours.get(2).getTourLogs().add(createTestTourLog(3));

        when(tourLogic.getAllToursList()).thenReturn(testTours);

        //Create a temporary directory and file path for testing
        Path tempDir = Files.createTempDirectory("tourplanner-test");
        Path tempFile = tempDir.resolve("test-tours.json");

        try {

            //captor for the tour that is imported again
            ArgumentCaptor<Tour> tourCaptor = ArgumentCaptor.forClass(Tour.class);

            //Act
            fileImportExportService.exportTours(tempFile.toString()); //export the test tour to the temp file
            fileImportExportService.importTours(tempFile.toString()); //import the test tour from the temp file

            //Assert
            verify(tourLogic, times(1)).getAllToursList();
            verify(tourLogic, times(testTours.size())).addTour(tourCaptor.capture());

            List<Tour> capturedTours = tourCaptor.getAllValues();

            //check if the imported tours are the same as the exported tours and if the tour logs are also exported/imported correctly
            //however some values (like e.g. duration) are not exported/imported because they are recalculated when the tour is imported
            //the id is also not exported/imported because it is generated when the tour is added to the database
            assert capturedTours.get(0).getName().equals(testTours.get(0).getName());
            assert capturedTours.get(0).getStartingPoint().equals(testTours.get(0).getStartingPoint());
            assert capturedTours.get(0).getDestinationPoint().equals(testTours.get(0).getDestinationPoint());
            assert capturedTours.get(0).getTourLogs().size() == 1;
            assert capturedTours.get(0).getTourLogs().get(0).getComment().equals(testTours.get(0).getTourLogs().get(0).getComment());


            assert capturedTours.get(1).getName().equals(testTours.get(1).getName());
            assert capturedTours.get(1).getStartingPoint().equals(testTours.get(1).getStartingPoint());
            assert capturedTours.get(1).getDestinationPoint().equals(testTours.get(1).getDestinationPoint());
            assert capturedTours.get(1).getTourLogs().size() == 1;
            assert capturedTours.get(1).getTourLogs().get(0).getComment().equals(testTours.get(1).getTourLogs().get(0).getComment());

            assert capturedTours.get(2).getName().equals(testTours.get(2).getName());
            assert capturedTours.get(2).getStartingPoint().equals(testTours.get(2).getStartingPoint());
            assert capturedTours.get(2).getDestinationPoint().equals(testTours.get(2).getDestinationPoint());
            assert capturedTours.get(2).getTourLogs().size() == 1;
            assert capturedTours.get(2).getTourLogs().get(0).getComment().equals(testTours.get(2).getTourLogs().get(0).getComment());

        } finally {
            // Clean up the temporary directory and file
            if (Files.exists(tempFile)) {
                Files.delete(tempFile);
            }
            if (Files.exists(tempDir)) {
                Files.delete(tempDir);
            }
        }
    }

    private ObservableList<Tour> createTestTour(){
        Tour testTour = new Tour();
        testTour.setName("Test Tour");
        testTour.setDuration(Duration.ofHours(1));
        testTour.setDistance(100);
        testTour.setTransportType(TransportType.CAR);
        testTour.setStartingPoint("Start");
        testTour.setDestinationPoint("End");
        testTour.setRating(5);
        testTour.setPathToMapImage("Path");
        testTour.setRating(5);
        testTour.setChildFriendlinessScore(5);
        testTour.setTourLogs(new ArrayList<>());

        List<Tour> testTourList = new ArrayList<>();
        testTourList.add(testTour);

        ObservableList<Tour> testTourObservableList = FXCollections.observableArrayList();
        testTourObservableList.setAll(testTourList);
        return testTourObservableList;

    }

    private ObservableList<Tour> createTestTours() {

        Tour testTour1 = new Tour();
        testTour1.setName("Tour 1");
        testTour1.setDuration(Duration.ofHours(3));
        testTour1.setDistance(100);
        testTour1.setTransportType(TransportType.CAR);
        testTour1.setStartingPoint("Start 1");
        testTour1.setDestinationPoint("End 1");
        testTour1.setRating(5);
        testTour1.setPathToMapImage("Path 1");
        testTour1.setRating(5);
        testTour1.setChildFriendlinessScore(5);
        testTour1.setTourLogs(new ArrayList<>());

        Tour testTour2 = new Tour();
        testTour2.setName("Tour 2");
        testTour2.setDuration(Duration.ofHours(2));
        testTour2.setDistance(200);
        testTour2.setTransportType(TransportType.BIKE);
        testTour2.setStartingPoint("Start 2");
        testTour2.setDestinationPoint("End 2");
        testTour2.setRating(4);
        testTour2.setPathToMapImage("Path 2");
        testTour2.setRating(4);
        testTour2.setChildFriendlinessScore(4);
        testTour2.setTourLogs(new ArrayList<>());

        Tour testTour3 = new Tour();
        testTour3.setName("Tour 3");
        testTour3.setDuration(Duration.ofHours(4));
        testTour3.setDistance(300);
        testTour3.setTransportType(TransportType.FEET);
        testTour3.setStartingPoint("Start 3");
        testTour3.setDestinationPoint("End 3");
        testTour3.setRating(3);
        testTour3.setPathToMapImage("Path 3");
        testTour3.setRating(3);
        testTour3.setChildFriendlinessScore(3);
        testTour3.setTourLogs(new ArrayList<>());


        List<Tour> testTours = new ArrayList<>();
        testTours.add(testTour1);
        testTours.add(testTour2);
        testTours.add(testTour3);

        ObservableList<Tour> testToursObservableList = FXCollections.observableArrayList();
        testToursObservableList.setAll(testTours);
        return testToursObservableList;
    }

    private TourLog createTestTourLog(int number) {
        TourLog testTourLog = new TourLog();
        testTourLog.setDate(new Date());
        testTourLog.setTotalTime(Duration.ofHours(number));
        testTourLog.setRating(number);
        testTourLog.setComment("Test Comment" + number);
        testTourLog.setDifficulty(Difficulty.MEDIUM);
        return testTourLog;
    }

}