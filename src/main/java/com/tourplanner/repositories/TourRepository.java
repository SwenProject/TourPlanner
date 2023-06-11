package com.tourplanner.repositories;

import com.tourplanner.models.Tour;

import com.tourplanner.services.interfaces.IConfigurationService;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Properties;

public class TourRepository implements ITourRepository {

    EntityManager entityManager;

    public TourRepository(IConfigurationService config) {

        // Load database credentials from config file
        Properties dbCredentials = new Properties();
        dbCredentials.setProperty("jakarta.persistence.jdbc.driver", config.getStringConfig("db.driver"));
        dbCredentials.setProperty("jakarta.persistence.jdbc.url", config.getStringConfig("db.url"));
        dbCredentials.setProperty("jakarta.persistence.jdbc.user", config.getStringConfig("db.username"));
        dbCredentials.setProperty("jakarta.persistence.jdbc.password", config.getStringConfig("db.password"));

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("tourPU", dbCredentials);
        entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Commit any pending changes
            entityManager.getTransaction().commit();

            // Close resources
            entityManager.close();
            entityManagerFactory.close();
        }));
    }

    @Override
    public void persist(Tour tour) {
        entityManager.persist(tour);
    }

    @Override
    public void delete(Tour tour) {
        entityManager.remove(tour);
    }

    @Override
    public ArrayList<Tour> getAll() {
        return new ArrayList<>(entityManager.createQuery("SELECT t FROM Tour t", Tour.class).getResultList());
    }

}
