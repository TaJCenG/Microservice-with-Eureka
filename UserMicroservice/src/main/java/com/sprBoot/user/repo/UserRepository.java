package com.sprBoot.user.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sprBoot.user.dao.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);  // Custom query (Topic 27)
    Page<User> findAll(Pageable pageable);     // Pagination (Topic 33)
}