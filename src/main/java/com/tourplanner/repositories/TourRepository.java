package com.tourplanner.repositories;

import com.tourplanner.models.Tour;

import com.tourplanner.services.interfaces.IConfigurationService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
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
    }

    @Override
    public void save(Tour tour) {
        entityManager.getTransaction().begin();
        entityManager.persist(tour);
        entityManager.getTransaction().commit();
        //tour id should be set now and is in tour object
    }

    @Override
    public void update(Tour tour) {
        entityManager.getTransaction().begin();
        entityManager.merge(tour);
        entityManager.getTransaction().commit();
    }

    @Override
    public void delete(Tour tour) {
        entityManager.getTransaction().begin();
        entityManager.remove(tour);
        entityManager.getTransaction().commit();
    }

    @Override
    public ArrayList<Tour> getAll() {
        return new ArrayList<>(entityManager.createQuery("SELECT t FROM Tour t", Tour.class).getResultList());
    }

    @Override
    public Tour getById(long id) {
        return entityManager.find(Tour.class, id);
    }

}
