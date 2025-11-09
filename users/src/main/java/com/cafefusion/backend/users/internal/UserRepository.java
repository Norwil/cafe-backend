package com.cafefusion.backend.users.internal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * This is the critical method Spring Security will use to load a user
     * by their "username", which in our case is their email address.
     *
     * @param email The email address to search for.
     * @return An optional containing the User if found.
     */
    Optional<User> findByEmail(String email);
}
