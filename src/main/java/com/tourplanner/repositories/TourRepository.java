package com.tourplanner.repositories;

import com.tourplanner.models.Tour;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;

public class TourRepository {

    EntityManager entityManager;

    public TourRepository() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("tourPU");
        entityManager = entityManagerFactory.createEntityManager();
    }

    public void save(Tour tour) {
        entityManager.getTransaction().begin();
        entityManager.persist(tour);
        entityManager.getTransaction().commit();
        //tour id should be set now and is in tour object
    }

    public void update(Tour tour) {
        entityManager.getTransaction().begin();
        entityManager.merge(tour);
        entityManager.getTransaction().commit();
    }

    public void delete(Tour tour) {
        entityManager.getTransaction().begin();
        entityManager.remove(tour);
        entityManager.getTransaction().commit();
    }

    public ArrayList<Tour> getAll() {
        return new ArrayList<>(entityManager.createQuery("SELECT t FROM Tour t", Tour.class).getResultList());
    }

}
