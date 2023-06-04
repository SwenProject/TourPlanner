package com.tourplanner.services;

import com.tourplanner.services.interfaces.IConfigurationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationServiceTest {

    @Test
    @DisplayName("Throws exception when config file is missing")
    public void testExceptionConfigFileMissing() {

        //Arrange
        //wrong path to a file that does not exist
        String filePath = "this/path/does/not/exist/config.properties";

        //Act & Assert
        assertThrows(IOException.class, () -> new ConfigurationService(filePath));
    }

    @Test
    @DisplayName("Throws exception when a property is missing in config file")
    public void testExceptionConfigPropertyMissing() throws IOException {

        //Arrange
        //Simulate a config file as a string
        //here db.password is missing (both the key and the value)
        String configFileData = """
        db.driver=org.example.Driver
        db.url=jdbc:postgresql://localhost:5432/db
        db.username=admin
        
        mapApi.routeCalculationEndpoint=http://example.com/route
        mapApi.imageEndpoint=http://example.com/image
        mapApi.key=API_KEY
        tourExport.fileExtension=tours
        log.logLevel=DEBUG
        """;

        InputStream inputStream = new ByteArrayInputStream(configFileData.getBytes());

        //Act
        IConfigurationService configService = new ConfigurationService(inputStream);

        //Assert
        assertThrows(IllegalArgumentException.class, configService::checkConfig);
    }

    @Test
    @DisplayName("Throws exception when a property does not have a value in config file")
    public void testExceptionConfigPropertyNoValue() throws IOException {

        //Arrange
        //Simulate a config file as a string
        //here db.password is not set (but the key is present)
        String configFileData = """
        db.driver=org.example.Driver
        db.url=jdbc:postgresql://localhost:5432/db
        db.username=admin
        db.password=
        mapApi.routeCalculationEndpoint=http://example.com/route
        mapApi.imageEndpoint=http://example.com/image
        mapApi.key=API_KEY
        tourExport.fileExtension=tours
        log.logLevel=DEBUG
        """;

        InputStream inputStream = new ByteArrayInputStream(configFileData.getBytes());

        //Act
        IConfigurationService configService = new ConfigurationService(inputStream);

        //Assert
        assertThrows(IllegalArgumentException.class, configService::checkConfig);
    }

    @Test
    @DisplayName("Throws exception when a API key still has the default value")
    public void testExceptionDefaultApiKeyValue() throws IOException {

        //Arrange
        //Simulate a config file as a string
        //here mapApi.key is still the default value
        String configFileData = """
        db.driver=org.example.Driver
        db.url=jdbc:postgresql://localhost:5432/db
        db.username=admin
        db.password=test
        mapApi.routeCalculationEndpoint=http://example.com/route
        mapApi.imageEndpoint=http://example.com/image
        mapApi.key=<YOUR API KEY HERE>
        tourExport.fileExtension=tours
        log.logLevel=DEBUG
        """;

        InputStream inputStream = new ByteArrayInputStream(configFileData.getBytes());

        //Act
        IConfigurationService configService = new ConfigurationService(inputStream);

        //Assert
        assertThrows(IllegalArgumentException.class, configService::checkConfig);
    }

    @Test
    @DisplayName("No Error when config file is valid")
    public void testConfigValid() throws IOException {

        //Arrange
        //Simulate a config file as a string
        String configFileData = """
        db.driver=org.example.Driver
        db.url=jdbc:postgresql://localhost:5432/db
        db.username=admin
        db.password=test
        mapApi.routeCalculationEndpoint=http://example.com/route
        mapApi.imageEndpoint=http://example.com/image
        mapApi.key=API_KEY
        tourExport.fileExtension=tours
        log.logLevel=DEBUG
        """;

        InputStream inputStream = new ByteArrayInputStream(configFileData.getBytes());

        //Act
        IConfigurationService configService = new ConfigurationService(inputStream);

        //Assert
        assertDoesNotThrow(configService::checkConfig);
    }

    @Test
    @DisplayName("Throws exception when trying to get a property that does not exist")
    public void testExceptionPropertyDoesNotExist() throws IOException {

        //Arrange
        //Simulate a config file as a string
        String configFileData = """
        db.driver=org.example.Driver
        db.url=jdbc:postgresql://localhost:5432/db
        db.username=admin
        db.password=test
        mapApi.routeCalculationEndpoint=http://example.com/route
        mapApi.imageEndpoint=http://example.com/image
        mapApi.key=API_KEY
        tourExport.fileExtension=tours
        log.logLevel=DEBUG
        """;

        InputStream inputStream = new ByteArrayInputStream(configFileData.getBytes());

        //Act
        IConfigurationService configService = new ConfigurationService(inputStream);

        //Assert
        assertDoesNotThrow(configService::checkConfig);
        assertThrows(IllegalArgumentException.class, () -> configService.getStringConfig("property.does.not.exist"));
    }

    @Test
    @DisplayName("Get a string config property value")
    public void testGetStringConfig() throws IOException {

        //Arrange
        //Simulate a config file as a string
        String configFileData = """
        db.driver=org.example.Driver
        db.url=jdbc:postgresql://localhost:5432/db
        db.username=admin
        db.password=test
        mapApi.routeCalculationEndpoint=http://example.com/route
        mapApi.imageEndpoint=http://example.com/image
        mapApi.key=API_KEY
        tourExport.fileExtension=tours
        log.logLevel=DEBUG
        """;

        InputStream inputStream = new ByteArrayInputStream(configFileData.getBytes());

        //Act
        IConfigurationService configService = new ConfigurationService(inputStream);

        //Assert
        assertDoesNotThrow(configService::checkConfig);
        assertEquals("org.example.Driver", configService.getStringConfig("db.driver"));
    }

}