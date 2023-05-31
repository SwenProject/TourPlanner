package com.tourplanner.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigurationService {
    private final Properties properties;

    public ConfigurationService(String filePath) throws IOException {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        }
    }

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

        if(properties.getProperty("mapApi.key") == null || properties.getProperty("mapApi.key").isEmpty() || properties.getProperty("mapApi.key").equals("<YOUR API KEY HERE>"))
            throw new IllegalArgumentException("Missing configuration value for key: " + "mapApi.key");

        //check for tour export config
        checkForConfig("tourExport.fileExtension");

        //check for log config
        checkForConfig("log.logLevel");
    }

    private void checkForConfig(String key){
        if(properties.getProperty(key) == null || properties.getProperty(key).isEmpty())
            throw new IllegalArgumentException("Missing configuration value for key: " + key);
    }

    public String getStringConfig(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Missing configuration value for key: " + key);
        }
        return value;
    }
}
