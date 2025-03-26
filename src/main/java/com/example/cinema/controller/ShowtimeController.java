package com.example.cinema.controller;

import com.example.cinema.model.Hall;
import com.example.cinema.model.Showtime;
import com.example.cinema.service.ShowtimeService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing showtimes in the cinema.
 */
@RestController
@RequestMapping("/api/showtimes")
public class ShowtimeController {

  private final ShowtimeService showtimeService;

  /**
   * Constructs a ShowtimeController with the specified ShowtimeService.
   *
   * @param showtimeService the service used for showtime operations
   */
  public ShowtimeController(ShowtimeService showtimeService) {
    this.showtimeService = showtimeService;
  }

  /**
   * Creates a new showtime for a specified hall.
   *
   * @param hallId the ID of the hall
   * @param request the showtime request containing film title and date
   * @return the created showtime
   */
  @PostMapping("/{hallId}")
  public Showtime createShowtime(@PathVariable Long hallId,
                                 @RequestBody ShowtimeRequest request) {
    LocalDateTime dateTime = LocalDateTime.parse(
            request.getDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    return showtimeService.createShowtime(hallId, request.getFilmTitle(), dateTime);
  }

  /**
   * Retrieves all showtimes.
   *
   * @return a list of all showtimes
   */
  @GetMapping
  public List<Showtime> getAllShowtimes() {
    return showtimeService.getAllShowtimes();
  }

  /**
   * Retrieves a showtime by ID.
   *
   * @param showtimeId the ID of the showtime
   * @return the found showtime
   */
  @GetMapping("/{showtimeId}")
  public Showtime getShowtimeById(@PathVariable Long showtimeId) {
    return showtimeService.getShowtimeById(showtimeId);
  }

  /**
   * Updates information about a showtime.
   *
   * @param showtimeId the ID of the showtime to update
   * @param request the showtime request containing updated details
   * @return the updated showtime
   */
  @PutMapping("/{showtimeId}")
  public Showtime updateShowtime(@PathVariable Long showtimeId,
                                 @RequestBody ShowtimeRequest request) {
    LocalDateTime dateTime = LocalDateTime.parse(
            request.getDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    Showtime updatedShowtime = new Showtime();
    updatedShowtime.setFilmTitle(request.getFilmTitle());
    updatedShowtime.setDateTime(dateTime);
    updatedShowtime.setHall(request.getHall());

    return showtimeService.updateShowtime(showtimeId, updatedShowtime);
  }

  /**
   * Deletes a showtime by its ID.
   *
   * @param showtimeId the ID of the showtime to delete
   */
  @DeleteMapping("/{showtimeId}")
  public void deleteShowtime(@PathVariable Long showtimeId) {
    showtimeService.deleteShowtime(showtimeId);
  }

  /**
   * DTO class for passing showtime data in JSON format.
   */
  public static class ShowtimeRequest {
    private String filmTitle;
    private String dateTime; // Format "yyyy-MM-dd'T'HH:mm:ss"
    private Hall hall;

    /**
     * Gets the film title.
     *
     * @return the film title
     */
    public String getFilmTitle() {
      return filmTitle;
    }

    /**
     * Sets the film title.
     *
     * @param filmTitle the film title to set
     */
    public void setFilmTitle(String filmTitle) {
      this.filmTitle = filmTitle;
    }

    /**
     * Gets the date and time of the showtime.
     *
     * @return the date and time
     */
    public String getDateTime() {
      return dateTime;
    }

    /**
     * Sets the date and time of the showtime.
     *
     * @param dateTime the date and time to set
     */
    public void setDateTime(String dateTime) {
      this.dateTime = dateTime;
    }

    /**
     * Gets the hall where the showtime is scheduled.
     *
     * @return the hall
     */
    public Hall getHall() {
      return hall;
    }

    /**
     * Sets the hall where the showtime is scheduled.
     *
     * @param hall the hall to set
     */
    public void setHall(Hall hall) {
      this.hall = hall;
    }
  }

  /**
   * Filters showtimes in a hall by film title (JPQL).
   *
   * @param hallId the ID of the hall
   * @param title the film title to search for
   * @return a list of showtimes matching the film title
   */
  @GetMapping("/filter/title/{hallId}")
  public List<Showtime> getShowtimesByTitle(@PathVariable Long hallId,
                                            @RequestParam String title) {
    return showtimeService.filterByTitle(hallId, title);
  }

  /**
   * Filters showtimes in a hall by date (Native SQL).
   *
   * @param hallId the ID of the hall
   * @param date the date to filter showtimes by (format: yyyy-MM-dd)
   * @return a list of showtimes on the specified date
   */
  @GetMapping("/filter/date/{hallId}")
  public List<Showtime> getShowtimesByDate(
          @PathVariable Long hallId,
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return showtimeService.filterByDate(hallId, date);
  }
}
