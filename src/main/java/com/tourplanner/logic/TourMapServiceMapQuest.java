package com.tourplanner.logic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;

import javafx.application.Platform;
import org.json.JSONObject;

import com.tourplanner.models.Tour;
import com.tourplanner.services.ITourMapService;

public class TourMapServiceMapQuest implements ITourMapService {


//TODO: transporttype ist noch hardgecoded
// request to staticMap API with SessionId and boundingBox
    public void calculateRoute(Tour tour){
        try{
            String url = "https://www.mapquestapi.com/directions/v2/optimizedroute" +
                    "?key=" + System.getenv("mapQuestAPIKey")
                    + "&from=" + tour.getStartingPoint()
                    + "&to=" + tour.getDestinationPoint()
                    + "&unit=k" //kilometer
                    + "&routeType=fastest" //options: fastest, shortest, pedestriann, bicycle
                    + "&doReverseGeocode=false"
                    + "&outFormat=json";

            // Send the HTTP GET request to the API endpoint
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            // Read the API response into a string
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());

            // Save route to access nested objects and values
            JSONObject route = jsonResponse.getJSONObject("route");

            //write to tourObject with Platform.runLater because Properties need to be updated on the JavaFX Application Thread
            Platform.runLater(() -> {
                tour.setDistance(route.getFloat("distance"));
                tour.setDuration(Duration.ofSeconds(route.getInt("time")));
            });

            // Retrieve sessionId and boundingBox for StaticMap request
            String sessionID = route.getString("sessionId");
            JSONObject boundingBox = route.getJSONObject("boundingBox");

            //TEST//
            // Print the value of the "formattedTime" key
            System.out.println("sessionId: " + sessionID);
            System.out.println("time: " + tour.getDuration().getSeconds());
            System.out.println("boundingBox: " + boundingBox);
            System.out.println("distance: " + tour.getDistance());



        } catch (Exception e) {
            e.printStackTrace();
        }

        //TODO: Map API calls for:
        // - distance and estimated time calculations -> write to tour object
        // - map image creation
        // - save path in Tour.pathToImage
    }

}
