package com.thesis.freelance.features.user.repository;

import com.thesis.freelance.features.user.entity.AdminInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminInviteRepository extends JpaRepository<AdminInvite, UUID> {

    Optional<AdminInvite> findByEmail(String email);

    List<AdminInvite> findAllByOrderByCreatedAtDesc();

}
