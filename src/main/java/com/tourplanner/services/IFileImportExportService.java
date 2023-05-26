package com.tourplanner.services;

public interface IFileImportExportService {
    public abstract void importTours(String path);
    public abstract void exportTours(String path);
}
