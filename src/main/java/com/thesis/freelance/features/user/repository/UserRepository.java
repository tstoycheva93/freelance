package com.thesis.freelance.features.user.repository;

import com.thesis.freelance.features.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    //to avoid LazyInitializationException in LoadUserByUsername method of CustomUserDetailsService
    @Query("SELECT u FROM User u JOIN FETCH u.userRole WHERE u.username = :username")
    Optional<User> findByUsernameWithRole(@Param("username") String username);
}
