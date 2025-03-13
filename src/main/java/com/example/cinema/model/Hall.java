package com.example.cinema.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;


/**
 * Represents a cinema hall.
 * Each hall has a unique ID, a name, and a list of showtimes.
 */
@Entity
@Table(name = "halls")
public class Hall {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Showtime> showtimes;

  /**
   * Default constructor.
   */
  public Hall() {}

  /**
   * Constructs a Hall with a given name.
   *
   * @param name the name of the hall
   */
  public Hall(String name) {
    this.name = name;
  }

  /**
   * Gets the ID of the hall.
   *
   * @return the hall ID
   */
  public Long getId() {
    return id;
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
   * @param name the new hall name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the list of showtimes associated with this hall.
   *
   * @return the list of showtimes
   */
  public List<Showtime> getShowtimes() {
    return showtimes;
  }

  /**
   * Sets the list of showtimes for this hall.
   *
   * @param showtimes the new list of showtimes
   */
  public void setShowtimes(List<Showtime> showtimes) {
    this.showtimes = showtimes;
  }
}
