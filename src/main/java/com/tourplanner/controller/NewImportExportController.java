package com.tourplanner.controller;

import com.tourplanner.TourPlannerApp;
import com.tourplanner.logic.TourLogic;
import com.tourplanner.services.interfaces.IConfigurationService;
import com.tourplanner.services.interfaces.IFileImportExportService;
import com.tourplanner.services.PdfService;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.awt.Desktop;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class NewImportExportController {

    private final TourLogic tourLogic;
    private final IConfigurationService config;
    private final IFileImportExportService fileImportExportService;
    private final PdfService pdfService;

    public GridPane newImportExportContainer;

    private static final Logger logger = LogManager.getLogger(NewImportExportController.class);

    public NewImportExportController(TourLogic tourLogic, IConfigurationService config, IFileImportExportService fileImportExportService, PdfService pdfService) {
        this.tourLogic = tourLogic;
        this.config = config;
        this.fileImportExportService = fileImportExportService;
        this.pdfService = pdfService;
    }

    public void initialize(){
    }


    public void onCreateNewTour() {
        tourLogic.createNewTour();
    }

    public void onImport() {

        logger.info("Importing tours from file. Opening file chooser.");

        FileChooser fileChooser = new FileChooser();

        // Set the initial directory (optional)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Set the extension filters (optional)
        FileChooser.ExtensionFilter toursFileExtension = new FileChooser.ExtensionFilter("Tour Planner Multiple Tours File (*." + config.getStringConfig("tourExport.fileExtension") + ")" , "*." + config.getStringConfig("tourExport.fileExtension"));
        fileChooser.getExtensionFilters().addAll(toursFileExtension);

        // Show the Save File dialog
        File selectedFile = fileChooser.showOpenDialog(newImportExportContainer.getScene().getWindow());

        // Check if a file was selected
        if (selectedFile != null) {
            logger.info("File selected. Importing tours from file.");
            String filePath = selectedFile.getAbsolutePath();
            this.fileImportExportService.importTours(filePath);
        }
    }

    public void onExport() {

        logger.info("Export tours dialog opened.");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Tours");
        alert.setHeaderText("How do you want to export the tours?");
        alert.initStyle(StageStyle.TRANSPARENT);

        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType buttonTypeToursFile = new ButtonType("To Tours File", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypePDF = new ButtonType("To PDF", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypePDF, buttonTypeToursFile);

        //add css file to alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(TourPlannerApp.class.getResource("css/alert-dialog.css")).toExternalForm());
        dialogPane.getStyleClass().add("deleteAlertBox");

        //replace alert image with custom image
        Image image = new Image(Objects.requireNonNull(TourPlannerApp.class.getResourceAsStream("images/export_tours.png")));
        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(70);
        alert.setGraphic(imageView);

        //set styleClass of buttonTypeToursFile
        Button toursFileButton = (Button) alert.getDialogPane().lookupButton(buttonTypeToursFile);
        toursFileButton.getStyleClass().add("toursFileButton");

        //set styleClass of buttonTypePDF
        Button pdfButton = (Button) alert.getDialogPane().lookupButton(buttonTypePDF);
        pdfButton.getStyleClass().add("pdfButton");

        //set background of scene to transparent so that the alert box can have rounded corners
        Scene scene = dialogPane.getScene();
        scene.setFill(Color.TRANSPARENT);

        //ensure that the alert box always appears in front of the main window on the same screen
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.initOwner(newImportExportContainer.getScene().getWindow());

        // show alert box and wait for response
        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeToursFile) {
                exportToToursFile();
            } else if (response == buttonTypePDF) {
                exportToursToPdf();
            }
        });
    }

    public void exportToToursFile(){

        logger.info("Exporting tours to file. Opening file chooser.");

        FileChooser fileChooser = new FileChooser();

        // Set the initial directory (optional)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Set the extension filters (optional)
        FileChooser.ExtensionFilter toursFileExtension = new FileChooser.ExtensionFilter("Tour Planner Multiple Tours File (*." + config.getStringConfig("tourExport.fileExtension") + ")" , "*." + config.getStringConfig("tourExport.fileExtension"));
        fileChooser.getExtensionFilters().addAll(toursFileExtension);

        // Show the Save File dialog
        File selectedFile = fileChooser.showSaveDialog(newImportExportContainer.getScene().getWindow());

        // Check if a file was selected
        if (selectedFile != null) {
            logger.info("File selected. Exporting tours to file.");
            String filePath = selectedFile.getAbsolutePath();
            this.fileImportExportService.exportTours(filePath);
        }
    }

    public void exportToursToPdf(){

        logger.info("Exporting tours to pdf. Opening file chooser.");

        FileChooser fileChooser = new FileChooser();

        // Set the initial directory (optional)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Set the extension filters (optional)
        FileChooser.ExtensionFilter pdfFileExtension = new FileChooser.ExtensionFilter("Pdf File (*.pdf)", "*.pdf");
        fileChooser.getExtensionFilters().addAll(pdfFileExtension);

        // Show the Save File dialog
        File selectedFile = fileChooser.showSaveDialog(newImportExportContainer.getScene().getWindow());

        // Check if a file was selected
        if (selectedFile != null) {
            logger.info("File selected. Exporting tours to pdf.");
            String filePath = selectedFile.getAbsolutePath();
            this.pdfService.createPdfSummary(tourLogic.getAllToursList(), filePath);

            //open pdf file with default pdf viewer
            try {
                logger.info("Opening pdf file with default pdf viewer.");
                Desktop.getDesktop().open(new File(filePath));
            } catch (IOException e) {
                logger.error("Error opening pdf file with default pdf viewer.", e);
            }
        }
    }
}