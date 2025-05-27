package com.example.cinema.service;

import com.example.cinema.cache.ShowtimeCache;
import com.example.cinema.dto.HallDto;
import com.example.cinema.dto.ShowtimeDto;
import com.example.cinema.model.Hall;
import com.example.cinema.model.Showtime;
import com.example.cinema.repository.HallRepository;
import com.example.cinema.repository.ShowtimeRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * Service class for managing showtimes.
 * Provides methods to create, retrieve, and update showtime records.
 */
@Service
public class ShowtimeService {

  private static final String SHOWTIME_NOT_FOUND = "Showtime not found";
  private static final String HALL_NOT_FOUND = "Hall not found";

  private final ShowtimeRepository showtimeRepository;
  private final HallRepository hallRepository;
  private final ShowtimeCache showtimeCache;


  /**
   * Constructs a ShowtimeService with the specified repositories and cache.
   *
   * @param showtimeRepository the repository for managing showtimes
   * @param hallRepository the repository for managing halls
   * @param showtimeCache the cache for storing showtimes
   */
  public ShowtimeService(ShowtimeRepository showtimeRepository,
                         HallRepository hallRepository,
                         ShowtimeCache showtimeCache) {
    this.showtimeRepository = showtimeRepository;
    this.hallRepository = hallRepository;
    this.showtimeCache = showtimeCache;
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
            .orElseThrow(() -> new RuntimeException(HALL_NOT_FOUND));

    Showtime showtime = new Showtime(dateTime, filmTitle, hall);
    showtimeCache.put(showtime.getId(), showtime);
    return showtimeRepository.save(showtime);
  }

  /**
   * Retrieves a showtime by its ID, first checking the cache.
   *
   * @param showtimeId the ID of the showtime to retrieve
   * @return the Showtime object
   */
  public Showtime getShowtimeById(Long showtimeId) {
    Showtime cachedShowtime = showtimeCache.get(showtimeId);
    if (cachedShowtime != null) {
      return cachedShowtime;
    }

    Showtime showtime = showtimeRepository.findById(showtimeId)
            .orElseThrow(() -> new RuntimeException(SHOWTIME_NOT_FOUND));

    showtimeCache.put(showtimeId, showtime);
    return showtime;
  }

  /**
   * Retrieves a list of all showtimes, first checking the cache.
   *
   * @return a list of all showtime records
   */
  public List<ShowtimeDto> getAllShowtimes() {
    return showtimeRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
  }

  private ShowtimeDto convertToDto(Showtime showtime) {
    Hall hall = showtime.getHall();
    HallDto hallDto = convertHallToDto(hall);

    return new ShowtimeDto(
            showtime.getId(),
            showtime.getDateTime(),
            showtime.getFilmTitle(),
            hallDto
    );
  }

  private HallDto convertHallToDto(Hall hall) {
    return new HallDto(
            hall.getId(),
            hall.getName(),
            hall.getCapacity()
    );
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
            .orElseThrow(() -> new RuntimeException(SHOWTIME_NOT_FOUND));

    Hall hall = hallRepository.findById(updatedShowtime.getHall().getId())
            .orElseThrow(() -> new RuntimeException(HALL_NOT_FOUND));

    existingShowtime.setFilmTitle(updatedShowtime.getFilmTitle());
    existingShowtime.setDateTime(updatedShowtime.getDateTime());
    existingShowtime.setHall(hall);

    showtimeCache.put(existingShowtime.getId(), existingShowtime);
    return showtimeRepository.save(existingShowtime);
  }

  /**
   * Deletes a showtime by its ID, also removing it from the cache.
   *
   * @param showtimeId the ID of the showtime to delete
   */
  public void deleteShowtime(Long showtimeId) {
    Showtime showtime = showtimeRepository.findById(showtimeId)
            .orElseThrow(() -> new RuntimeException(SHOWTIME_NOT_FOUND));

    showtimeRepository.delete(showtime);
    showtimeCache.remove(showtimeId);
  }

  /**
   * Filters showtimes by film title in a specific hall.
   *
   * @param hallId the ID of the hall
   * @param filmTitle the title of the film
   * @return a list of showtimes matching the given criteria
   */
  public List<Showtime> filterByTitle(Long hallId, String filmTitle) {
    return showtimeRepository.findByHallIdAndFilmTitleContainingIgnoreCase(hallId, filmTitle);
  }

  /**
   * Filters showtimes by a specific date in a specific hall.
   *
   * @param hallId the ID of the hall
   * @param date the date to filter by
   * @return a list of showtimes for the given date
   */
  public List<Showtime> filterByDate(Long hallId, LocalDate date) {
    return showtimeRepository.findByHallIdAndDate(hallId, date);
  }
}