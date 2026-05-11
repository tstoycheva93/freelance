package com.thesis.freelance.features.category.repository;

import com.thesis.freelance.common.enums.ActivityStatus;
import com.thesis.freelance.features.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByName(String name);

    @Query("SELECT c FROM Category c WHERE c.activityStatus = :activityStatus")
    List<Category> findAllByActivityStatus(@Param("activityStatus") ActivityStatus activityStatus);
}
