package com.he180773.testreact.repository;


import com.he180773.testreact.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    Optional<User> findById(Long id);

    List<User> searchAllByNameContains(String username);

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
