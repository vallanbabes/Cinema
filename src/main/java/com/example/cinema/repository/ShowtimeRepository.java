package com.example.cinema.repository;

import com.example.cinema.model.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing {@link Showtime} entities.
 * Extends {@link JpaRepository} to provide CRUD operations for the Showtime entity.
 */
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
}
