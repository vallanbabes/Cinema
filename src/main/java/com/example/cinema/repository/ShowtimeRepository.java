package com.example.cinema.repository;

import com.example.cinema.model.Showtime;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing Showtime entities.
 */
@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {

  /**
   * Finds showtimes in a hall that contain the given film title (case-insensitive).
   *
   * @param hallId the ID of the hall
   * @param title the film title to search for
   * @return a list of matching showtimes
   */
  @Query("SELECT s FROM Showtime s WHERE s.hall.id = :hallId "
          + "AND LOWER(s.filmTitle) LIKE LOWER(CONCAT('%', :title, '%'))")
  List<Showtime> findByHallIdAndFilmTitleContainingIgnoreCase(
          @Param("hallId") Long hallId, @Param("title") String title);

  /**
   * Finds showtimes in a hall on a specific date.
   *
   * @param hallId the ID of the hall
   * @param date the date of the showtime
   * @return a list of showtimes scheduled on the given date
   */
  @Query(value = "SELECT * FROM showtimes s WHERE s.hall_id = :hallId "
          + "AND DATE(s.date_time) = :date", nativeQuery = true)
  List<Showtime> findByHallIdAndDate(
          @Param("hallId") Long hallId, @Param("date") LocalDate date);
}
