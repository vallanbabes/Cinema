package com.example.cinema.repository;

import com.example.cinema.model.Hall;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Hall} entities.
 * Extends {@link JpaRepository} to provide CRUD operations for the Hall entity.
 */
public interface HallRepository extends JpaRepository<Hall, Long> {
}