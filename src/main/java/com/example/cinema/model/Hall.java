package com.example.cinema.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Represents a cinema hall where movie showtimes take place.
 * Contains information about hall name, capacity and scheduled showtimes.
 */
@Entity
@Table(name = "halls")
public class Hall {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "Hall name is required")
  @Size(max = 10, message = "Hall name must be less than 10 characters")
  private String name;

  @NotNull(message = "Capacity is required")
  @Min(value = 1, message = "Capacity must be at least 1")
  @Max(value = 300, message = "Capacity must not exceed 300")
  private Integer capacity;

  @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Showtime> showtimes;

  /**
   * Default constructor.
   */
  public Hall() {
  }

  /**
   * Constructs a Hall with the specified ID.
   *
   * @param id the unique identifier for the hall
   */
  public Hall(Long id) {
    this.id = id;
  }

  /**
   * Constructs a Hall with the specified name and capacity.
   *
   * @param name the name of the hall
   * @param capacity the seating capacity of the hall
   */
  public Hall(String name, Integer capacity) {
    this.name = name;
    this.capacity = capacity;
  }

  /**
   * Gets the unique identifier of the hall.
   *
   * @return the hall ID
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the unique identifier of the hall.
   *
   * @param id the hall ID to set
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * Gets the name of the hall.
   *
   * @return the hall name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of the hall.
   *
   * @param name the hall name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the seating capacity of the hall.
   *
   * @return the hall capacity
   */
  public Integer getCapacity() {
    return capacity;
  }

  /**
   * Sets the seating capacity of the hall.
   *
   * @param capacity the hall capacity to set
   */
  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }

  /**
   * Gets the list of showtimes scheduled in this hall.
   *
   * @return list of showtimes
   */
  public List<Showtime> getShowtimes() {
    return showtimes;
  }

  /**
   * Sets the list of showtimes scheduled in this hall.
   *
   * @param showtimes list of showtimes to set
   */
  public void setShowtimes(List<Showtime> showtimes) {
    this.showtimes = showtimes;
  }
}