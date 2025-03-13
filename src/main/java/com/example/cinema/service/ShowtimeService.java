package com.example.cinema.service;


import com.example.cinema.model.Hall;
import com.example.cinema.model.Showtime;
import com.example.cinema.repository.HallRepository;
import com.example.cinema.repository.ShowtimeRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service class for managing showtimes.
 * Provides methods to create, retrieve, and update showtime records.
 */
@Service
public class ShowtimeService {

  private final ShowtimeRepository showtimeRepository;
  private final HallRepository hallRepository;

  /**
   * Constructs a ShowtimeService with the specified repositories.
   *
   * @param showtimeRepository the repository for managing showtimes
   * @param hallRepository the repository for managing halls
   */
  public ShowtimeService(ShowtimeRepository showtimeRepository, HallRepository hallRepository) {
    this.showtimeRepository = showtimeRepository;
    this.hallRepository = hallRepository;
  }

  /**
   * Creates a new showtime for a given hall.
   *
   * @param hallId the ID of the hall where the showtime will be scheduled
   * @param filmTitle the title of the film
   * @param dateTime the date and time of the showtime
   * @return the created Showtime object
   */
  public Showtime createShowtime(Long hallId, String filmTitle, LocalDateTime dateTime) {
    Hall hall = hallRepository.findById(hallId)
            .orElseThrow(() -> new RuntimeException("Hall not found"));

    Showtime showtime = new Showtime(dateTime, filmTitle, hall);
    return showtimeRepository.save(showtime);
  }

  /**
   * Retrieves a list of all showtimes.
   *
   * @return a list of all showtime records
   */
  public List<Showtime> getAllShowtimes() {
    return showtimeRepository.findAll();
  }

  /**
   * Updates an existing showtime with new details.
   *
   * @param showtimeId the ID of the showtime to update
   * @param updatedShowtime the new showtime details
   * @return the updated Showtime object
   */
  public Showtime updateShowtime(Long showtimeId, Showtime updatedShowtime) {
    Showtime existingShowtime = showtimeRepository.findById(showtimeId)
            .orElseThrow(() -> new RuntimeException("Showtime not found"));

    Hall hall = hallRepository.findById(updatedShowtime.getHall().getId())
            .orElseThrow(() -> new RuntimeException("Hall not found"));

    // Updating showtime details
    existingShowtime.setFilmTitle(updatedShowtime.getFilmTitle());
    existingShowtime.setDateTime(updatedShowtime.getDateTime());
    existingShowtime.setHall(hall);

    return showtimeRepository.save(existingShowtime);
  }
  /**
   * Deletes a showtime by its ID.
   *
   * @param showtimeId the ID of the showtime to delete
   */

  public void deleteShowtime(Long showtimeId) {
    Showtime showtime = showtimeRepository.findById(showtimeId)
            .orElseThrow(() -> new RuntimeException("Showtime not found"));

    // **Удаляем сеанс**
    showtimeRepository.delete(showtime);
  }
}
