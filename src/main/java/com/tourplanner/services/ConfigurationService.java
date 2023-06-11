package com.tourplanner.services;

import com.tourplanner.services.interfaces.IConfigurationService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigurationService implements IConfigurationService {
    private final Properties properties;

    public ConfigurationService(String filePath) throws IOException {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        }
    }

    //constructor with direct input stream for testing
    public ConfigurationService(InputStream inputStream) throws IOException {
        properties = new Properties();
        properties.load(inputStream);
    }

    @Override
    public void checkConfig(){

        //check for db config
        checkForConfig("db.driver");
        checkForConfig("db.url");
        checkForConfig("db.username");
        checkForConfig("db.password");

        //check for map api config
        checkForConfig("mapApi.routeCalculationEndpoint");
        checkForConfig("mapApi.imageEndpoint");
        checkForConfig("mapApi.key");
        if(properties.getProperty("mapApi.key").equals("<YOUR API KEY HERE>"))
            throw new IllegalArgumentException("Missing configuration value for key: " + "mapApi.key");

        //check for openAi api config
        checkForConfig("openAi.model");
        checkForConfig("openAi.key");
        if(properties.getProperty("openAi.key").equals("<YOUR API KEY HERE>"))
            throw new IllegalArgumentException("Missing configuration value for key: " + "openAi.key");

        //check for tour export config
        checkForConfig("tourExport.fileExtension");

        //check for log config
        checkForConfig("log.logLevel");
    }

    private void checkForConfig(String key){
        if(properties.getProperty(key) == null || properties.getProperty(key).isEmpty())
            throw new IllegalArgumentException("Missing configuration value for key: " + key);
    }

    @Override
    public String getStringConfig(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Missing configuration value for key: " + key);
        }
        return value;
    }
}
