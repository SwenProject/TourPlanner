package com.tourplanner.logic;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;


import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

import com.tourplanner.services.ConfigurationService;
import javafx.application.Platform;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.tourplanner.models.Tour;
import com.tourplanner.services.ITourMapService;

public class TourMapServiceMapQuest implements ITourMapService {

    private final ConfigurationService config;

    private static final Logger logger = LogManager.getLogger(TourMapServiceMapQuest.class);

    public TourMapServiceMapQuest(ConfigurationService config) {
        this.config = config;
    }

    public void calculateRoute(Tour tour) {

        logger.info("Calculating route for tour: \"" + tour.getName() + "\" with transport type \"" + tour.getTransportType() + "\" from \"" + tour.getStartingPoint() + "\" to \"" + tour.getDestinationPoint() + "\"");

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
            // we set the duration and distance to -1 earlier so that the loading spinner appears immediately
            tour.setPathToMapImage("loading");
        });

        try {

            String url = config.getStringConfig("mapApi.routeCalculationEndpoint") +
                    "?key=" + config.getStringConfig("mapApi.key")
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

            float distance = route.getFloat("distance");
            Duration duration = Duration.ofSeconds(route.getInt("time"));

            //write to tourObject with Platform.runLater because Properties need to be updated on the JavaFX Application Thread
            Platform.runLater(() -> {
                tour.setDistance(distance);
                tour.setDuration(duration);
            });

            // Retrieve sessionId and boundingBox for StaticMap request
            String sessionID = route.getString("sessionId");
            JSONObject boundingBox = route.getJSONObject("boundingBox");

            logger.info("Route calculation successful, downloading map image");

            //Make API Request and store Map jpeg to directory
            getMapImageFromAPI(tour, sessionID, boundingBox);


        } catch (JSONException e) {
            logger.warn("Could not calculate route for tour \"" + tour.getName() + "\"");
            Platform.runLater(() -> {
                // -2 so that frontend can display error message
                tour.setDistance(-2);
                tour.setDuration(Duration.ofSeconds(-2));
                // "error" because if null frontend displays loading spinner
                tour.setPathToMapImage("error");
            });

        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                // -2 so that frontend can display error message
                tour.setDistance(-2);
                tour.setDuration(Duration.ofSeconds(-2));
                // "error" because if null frontend displays loading spinner
                tour.setPathToMapImage("error");
            });
        }
    }

    void getMapImageFromAPI(Tour tour, String sessionId, JSONObject boundingBox){
        try {
            String url = config.getStringConfig("mapApi.imageEndpoint")
                    + "?key=" + config.getStringConfig("mapApi.key")
                    + "&boundingBox="
                    //upper left
                        + boundingBox.getJSONObject("ul").getDouble("lat") + ","
                        + boundingBox.getJSONObject("ul").getDouble("lng") + ","
                    //lower right
                        + boundingBox.getJSONObject("lr").getDouble("lat") + ","
                        + boundingBox.getJSONObject("lr").getDouble("lng")
                    + "&session=" + sessionId
                    + "&size=1920,1920@2x";

            // Send the HTTP GET request to the API endpoint
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            // Get the input stream of the response
            InputStream inputStream = connection.getInputStream();

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

            logger.info("Map image downloaded successfully");

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
