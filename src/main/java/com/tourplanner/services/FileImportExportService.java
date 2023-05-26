package com.tourplanner.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tourplanner.logic.TourLogic;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourplanner.models.Tour;
import com.tourplanner.services.JsonSerializers.DurationDeserializer;
import com.tourplanner.services.JsonSerializers.DurationSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FileImportExportService implements IFileImportExportService {

    private final TourLogic tourLogic;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public FileImportExportService (TourLogic tourLogic) {
        this.tourLogic = tourLogic;

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Duration.class, new DurationSerializer());
        simpleModule.addDeserializer(Duration.class, new DurationDeserializer());
        this.objectMapper.registerModule(simpleModule);
        objectMapper.configure(MapperFeature.AUTO_DETECT_FIELDS, false);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void importTours(String pathString) {
        try {
            Path path = Paths.get(pathString);
            File file = path.toFile();
            ArrayList<Tour> importedToursList = objectMapper.readValue(file, new TypeReference<ArrayList<Tour>>(){});

            for (Tour tour : importedToursList){
                tourLogic.addTour(tour);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void exportTours(String pathString) {
        try {
            Path path = Paths.get(pathString);
            File file = path.toFile();
            objectMapper.writeValue(file, tourLogic.getAllToursList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
