package com.thesis.freelance.features.location.repository;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.location.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {

    Optional<Location> findByName(String name);
    @Query("SELECT l FROM Location l WHERE l.name LIKE %?1%")
    //Iterable<Location> findAll(String name);
    Set<Location> findAll(String name);

    @Query("SELECT l FROM Location l WHERE l.activityStatus = :activityStatus")
    List<Location> findAllByActivityStatus(@Param("activityStatus") ActivityStatus activityStatus);

    @Query("SELECT l FROM Location l WHERE LOWER(l.name) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY l.name")
    List<Location> searchByName(@Param("query") String query);
}