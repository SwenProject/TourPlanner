package com.tourplanner.logic;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import javafx.application.Platform;
import org.json.JSONException;
import org.json.JSONObject;

import com.tourplanner.models.Tour;
import com.tourplanner.services.ITourMapService;

public class TourMapServiceMapQuest implements ITourMapService {

    public void calculateRoute(Tour tour) {

        //delete old image from filesystem if valid path
        try {
            if (tour.getPathToMapImage() != null && !tour.getPathToMapImage().equals("error")) {
                Files.delete(Paths.get(tour.getPathToMapImage()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //write to tourObject with Platform.runLater because Properties need to be updated on the JavaFX Application Thread
        Platform.runLater(() -> {
            tour.setDistance(-1); //set distance and duration to -1 to indicate that the request is in progress
            tour.setDuration(Duration.ofSeconds(-1));
            tour.setPathToMapImage("loading");
        });

        try {

            String url = "https://www.mapquestapi.com/directions/v2/optimizedroute" +
                    "?key=" + System.getenv("mapQuestAPIKey")
                    + "&from=" + tour.getStartingPoint()
                    + "&to=" + tour.getDestinationPoint()
                    + "&unit=k" //kilometer
                    + "&doReverseGeocode=false"
                    + "&outFormat=json";

            switch (tour.getTransportType()) {
                case CAR -> url += "&routeType=fastest";
                case FEET -> url += "&routeType=pedestrian";
                case BIKE -> url += "&routeType=bicycle";
                default -> throw new IllegalArgumentException("Invalid transport type");
            }

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

            //Make API Request and store Map jpeg to directory
            getMapImageFromAPI(tour, sessionID, boundingBox);


        } catch (JSONException e) {
            Platform.runLater(() -> {
                // -2 so that frontend can display error message
                tour.setDistance(-2);
                tour.setDuration(Duration.ofSeconds(-2));
                // "error" because if null frontend displays loading spinner
                tour.setPathToMapImage("error");
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getMapImageFromAPI(Tour tour, String sessionId, JSONObject boundingBox){
        try {
            String url = "https://www.mapquestapi.com/staticmap/v5/map"
                    + "?key=" + System.getenv("mapQuestAPIKey")
                    + "&boundingBox="
                    //upper left
                        + boundingBox.getJSONObject("ul").getDouble("lat") + ","
                        + boundingBox.getJSONObject("ul").getDouble("lng") + ","
                    //lower right
                        + boundingBox.getJSONObject("lr").getDouble("lat") + ","
                        + boundingBox.getJSONObject("lr").getDouble("lng")
                    + "&session=" + sessionId
                    + "&size=1920,1920@2x";

            //TEST
            System.out.println(url);

            // Send the HTTP GET request to the API endpoint
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            // Get the input stream of the response
            InputStream inputStream = connection.getInputStream();

            //TEST
            //System.out.println(Arrays.toString(inputStream.readNBytes(100)));

            // Create the directory if it doesn't exist
            Path parentDirectory = Paths.get("./src/main/resources/com/tourplanner/");  // Relative path to go two directories up
            Path directory = parentDirectory.resolve("map_images");
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // Define the file path and name
            String fileName = randomNumberGenerator() + ".jpeg";
            Path filePath = directory.resolve(fileName);

            // Store the image file
            FileOutputStream outputStream = new FileOutputStream(filePath.toFile());
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.close();
            inputStream.close();

            //write path to tour object
            Platform.runLater(() -> {
                tour.setPathToMapImage(filePath.toString());
            });

            System.out.println("Image saved successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int randomNumberGenerator() {
        Random random = new Random();
        int min = 100000000;  // Minimum value with 9 digits
        int max = 999999999;  // Maximum value with 9 digits

        return random.nextInt(max - min + 1) + min;
    }

}
