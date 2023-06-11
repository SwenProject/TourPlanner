package com.tourplanner.services;

import com.tourplanner.models.Tour;
import com.tourplanner.models.TourLog;
import com.tourplanner.services.interfaces.IAiSummaryService;
import com.tourplanner.services.interfaces.IConfigurationService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AiSummaryService implements IAiSummaryService {

    private final IConfigurationService config;

    public AiSummaryService(IConfigurationService config) {
        this.config = config;
    }

    @Override
    public String generateAiSummary(Tour tour) throws JSONException, IOException { //make request to OpenAI API
        String url = "https://api.openai.com/v1/chat/completions";

        //request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", config.getStringConfig("openAi.model"));
        requestBody.put("max_tokens", 256);
        requestBody.put("temperature", 1);

        JSONArray messages = new JSONArray();

        JSONObject systemMessage = new JSONObject();
        systemMessage.put("role", "system");
        systemMessage.put("content", getPrompt());
        messages.put(systemMessage);

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", convertTourToJsonString(tour));
        messages.put(userMessage);

        requestBody.put("messages", messages);

        // Create URL object
        URL apiUrl = new URL(url);

        // Create connection object
        HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();

        // Set request method
        connection.setRequestMethod("POST");

        // Set request headers
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + config.getStringConfig("openAi.key"));

        // Enable input and output streams
        connection.setDoOutput(true);
        connection.setDoInput(true);

        // Write the JSON body to the request
        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
        outputStream.close();

        // Read the response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        String responseString = response.toString();

        // Close the connection
        connection.disconnect();

        //get message from the response
        JSONObject responseJson = new JSONObject(responseString);
        JSONArray choices = responseJson.getJSONArray("choices");
        JSONObject choice = choices.getJSONObject(0);
        JSONObject message = choice.getJSONObject("message");

        return message.getString("content");
    }

    private String convertTourToJsonString(Tour tour) throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject tourJson = new JSONObject();
        json.put("tour", tourJson);

        JSONObject tourInfoJson = new JSONObject();
        tourInfoJson.put("starting-point", tour.getStartingPoint());
        tourInfoJson.put("destination", tour.getDestinationPoint());

        String distance = String.format("%.0f", tour.getDistance()) + "km";
        tourInfoJson.put("distance", distance);

        tourInfoJson.put("transportation-type", tour.getTransportType().name());
        tourJson.put("tour-info", tourInfoJson);

        JSONArray tourCommentsJson = new JSONArray();
        for (TourLog log : tour.getTourLogs()) {
            JSONObject logJson = new JSONObject();
            logJson.put("rating", log.getRating());
            logJson.put("text", log.getComment());
            tourCommentsJson.put(logJson);
        }
        tourJson.put("tour-comments", tourCommentsJson);

        return json.toString();
    }

    public String getPrompt(){
        return """
                You are a helpful tool for summarizing information on a tour and providing interesting facts about the tour.
                You will be provided with information on the tour in JSON format. Instead of referring to the direct enum values of transport-type, refer to them like this:
                refer to CAR as "by car"
                refer to FEET as "by foot"
                refer to BIKE as "by bike"
                A tour is a route that people can take by foot, bike, or car (indicated by the transport-type). It has a starting-point, destination, and distance. Please give a short overview of the information provided. In case the transport-type doesn’t make sense for a certain tour (for example a tour with a distance of 200km would be a bad idea when the transport-type is indicated as FEET), mention this in the beginning of the summary, as this might be an accidental error made when the tour was created.
                A tour also has comments made by users providing insight on their experience with the tour. Each tour comment consists of a rating between 1 and 5 stars and a text. You will be provided with all comments on the tour. Please incorporate these comments into your summary. Talk about the general sentiment on the tour and point out important things to note, advantages and disadvantages, and other things to consider when taking the route. However, the reader is not going to be interested in any individual stories or anecdotes included in the comments.
                If there are few or no comments on the tour, or there is not much important information in the comments, fill the space by giving some fun or interesting facts about the starting point and destination, and what users can likely expect when going on the tour. This is only applicable if the starting point and destination are broad areas (like cities). Do not try to interpret very specific addresses.
                The summary should have between 2 and 3 short paragraphs. Be sure to include any safety-related information, like comments on taking the tour in winter or under difficult conditions.
                """;
    }
}
