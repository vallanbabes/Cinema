package com.example.cinema.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Represents a showtime for a film in a cinema hall.
 */
@Entity
@Table(name = "showtimes")
public class Showtime {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private LocalDateTime dateTime;

  @NotBlank(message = "Film title is required")
  @Size(max = 100, message = "Film title must be less than 100 characters")
  private String filmTitle;

  @ManyToOne
  @JoinColumn(name = "hall_id", nullable = false)
  @JsonIgnore
  private Hall hall;

  /**
   * Default constructor.
   */
  public Showtime() {
  }

  /**
   * Constructs a Showtime with the specified date, film title, and hall.
   *
   * @param dateTime the date and time of the showtime
   * @param filmTitle the title of the film
   * @param hall the hall where the showtime is scheduled
   */
  public Showtime(LocalDateTime dateTime, String filmTitle, Hall hall) {
    this.dateTime = dateTime;
    this.filmTitle = filmTitle;
    this.hall = hall;
  }

  /**
   * Gets the ID of the showtime.
   *
   * @return the showtime ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the ID of the showtime.
   *
   * @param id the showtime ID to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the date and time of the showtime.
   *
   * @return the date and time
   */
  public LocalDateTime getDateTime() {
    return dateTime;
  }

  /**
   * Sets the date and time of the showtime.
   *
   * @param dateTime the date and time to set
   */
  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }

  /**
   * Gets the title of the film.
   *
   * @return the film title
   */
  public String getFilmTitle() {
    return filmTitle;
  }

  /**
   * Sets the title of the film.
   *
   * @param filmTitle the film title to set
   */
  public void setFilmTitle(String filmTitle) {
    this.filmTitle = filmTitle;
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