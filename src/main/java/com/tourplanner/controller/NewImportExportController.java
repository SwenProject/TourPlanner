package com.tourplanner.controller;

import com.tourplanner.TourPlannerApp;
import com.tourplanner.logic.TourLogic;
import javafx.event.ActionEvent;

public class NewImportExportController {

    private final TourLogic tourLogic;

    public GridPane newImportExportContainer;

    public NewImportExportController(TourLogic tourLogic) {
        this.tourLogic = tourLogic;
    }

    public void initialize(){
    }


    public void onCreateNewTour(ActionEvent actionEvent) {
        tourLogic.createNewTour();
    }

    public void onImport(ActionEvent actionEvent) {
    }

    public void onExport(ActionEvent actionEvent) {
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
        FileChooser fileChooser = new FileChooser();

        // Set the initial directory (optional)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Set the extension filters (optional)
        FileChooser.ExtensionFilter toursFileExtension = new FileChooser.ExtensionFilter("Tour Planner Multiple Tours File (*.tours)", "*.tours");
        fileChooser.getExtensionFilters().addAll(toursFileExtension);

        // Show the Save File dialog
        File selectedFile = fileChooser.showSaveDialog(newImportExportContainer.getScene().getWindow());

        // Check if a file was selected
        if (selectedFile != null) {
            String filePath = selectedFile.getAbsolutePath();

            // Use the file path in your JavaFX code
            // For example, you can display the path in a TextField
            System.out.println("Filepath:" + filePath);
        }
    }

    public void exportToursToPdf(){
        //TODO: Integrate pdf generation service
    }
}