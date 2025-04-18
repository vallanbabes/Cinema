package com.example.cinema.controller;

import com.example.cinema.model.Hall;
import com.example.cinema.model.Showtime;
import com.example.cinema.service.ShowtimeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
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

@RestController
@RequestMapping("/api/showtimes")
@Tag(name = "Showtimes", description = "Movie showtimes management")
public class ShowtimeController {

  private final ShowtimeService showtimeService;

  public ShowtimeController(ShowtimeService showtimeService) {
    this.showtimeService = showtimeService;
  }

  @Operation(summary = "Create new showtime")
  @PostMapping("/{hallId}")
  public Showtime createShowtime(
          @Parameter(description = "ID of the cinema hall") @PathVariable Long hallId,
          @RequestBody ShowtimeRequest request) {
    LocalDateTime dateTime = LocalDateTime.parse(
            request.getDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    return showtimeService.createShowtime(hallId, request.getFilmTitle(), dateTime);
  }

  @Operation(summary = "Get all showtimes")
  @GetMapping
  public List<Showtime> getAllShowtimes() {
    return showtimeService.getAllShowtimes();
  }

  @Operation(summary = "Get showtime by ID")
  @GetMapping("/{showtimeId}")
  public Showtime getShowtimeById(
          @Parameter(description = "ID of the showtime") @PathVariable Long showtimeId) {
    return showtimeService.getShowtimeById(showtimeId);
  }

  @Operation(summary = "Update showtime")
  @PutMapping("/{showtimeId}")
  public Showtime updateShowtime(
          @Parameter(description = "ID of the showtime to update") @PathVariable Long showtimeId,
          @RequestBody ShowtimeRequest request) {
    LocalDateTime dateTime = LocalDateTime.parse(
            request.getDateTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    Showtime updatedShowtime = new Showtime();
    updatedShowtime.setFilmTitle(request.getFilmTitle());
    updatedShowtime.setDateTime(dateTime);
    updatedShowtime.setHall(request.getHall());

    return showtimeService.updateShowtime(showtimeId, updatedShowtime);
  }

  @Operation(summary = "Delete showtime")
  @DeleteMapping("/{showtimeId}")
  public void deleteShowtime(
          @Parameter(description = "ID of the showtime to delete") @PathVariable Long showtimeId) {
    showtimeService.deleteShowtime(showtimeId);
  }

  @Operation(summary = "Filter showtimes by movie title")
  @GetMapping("/filter/title/{hallId}")
  public List<Showtime> getShowtimesByTitle(
          @Parameter(description = "ID of the cinema hall") @PathVariable Long hallId,
          @Parameter(description = "Movie title to search for") @RequestParam String title) {
    return showtimeService.filterByTitle(hallId, title);
  }

  @Operation(summary = "Filter showtimes by date")
  @GetMapping("/filter/date/{hallId}")
  public List<Showtime> getShowtimesByDate(
          @Parameter(description = "ID of the cinema hall") @PathVariable Long hallId,
          @Parameter(description = "Date to filter by (format: yyyy-MM-dd)", example = "2023-12-31")
          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return showtimeService.filterByDate(hallId, date);
  }

  @Schema(description = "Request object for creating/updating showtimes")
  public static class ShowtimeRequest {
    @Schema(description = "Title of the movie", example = "Inception")
    private String filmTitle;

    @Schema(description = "Date and time of the showtime (ISO format)", example = "2023-12-31T18:30:00")
    private String dateTime;

    @Schema(description = "Cinema hall information")
    private Hall hall;

    public String getFilmTitle() {
      return filmTitle;
    }

    public void setFilmTitle(String filmTitle) {
      this.filmTitle = filmTitle;
    }

    public String getDateTime() {
      return dateTime;
    }

    public void setDateTime(String dateTime) {
      this.dateTime = dateTime;
    }

    public Hall getHall() {
      return hall;
    }

    public void setHall(Hall hall) {
      this.hall = hall;
    }
  }
}