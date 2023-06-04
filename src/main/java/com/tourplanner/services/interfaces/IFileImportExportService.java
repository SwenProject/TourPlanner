package com.tourplanner.services.interfaces;

public interface IFileImportExportService {
    void importTours(String path);
    void exportTours(String path);
}
