package com.thesis.freelance.features.location.repository;

import com.thesis.freelance.features.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    Optional<Location> findByName(String name);
    @Query("SELECT l FROM Location l WHERE l.name LIKE %?1%")
    //Iterable<Location> findAll(String name);
    Set<Location> findAll(String name);
}