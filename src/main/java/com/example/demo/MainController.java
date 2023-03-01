package com.example.demo;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class MainController {


    public VBox advancedSearchContainer;
    public Button advancedSearchButton;

    public void initialize(){

    }


    public void toggleAdvancedSearch(ActionEvent actionEvent) {
        advancedSearchContainer.setVisible(!advancedSearchContainer.isVisible());
        advancedSearchContainer.setManaged(!advancedSearchContainer.isManaged());
        advancedSearchButton.setText(advancedSearchContainer.isVisible() ? "Show Advanced Search" : "Hide Advanced Search");
    }
}