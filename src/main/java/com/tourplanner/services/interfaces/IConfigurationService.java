package com.tourplanner.services.interfaces;

public interface IConfigurationService {
    void checkConfig();

    String getStringConfig(String key);
}
