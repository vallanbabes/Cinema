package com.example.cinema.cache;

import com.example.cinema.model.Showtime;
import org.springframework.stereotype.Component;

/**
 * Cache for storing Showtime objects using LFU (Least Frequently Used) eviction strategy.
 * This cache holds a limited number of Showtime objects and removes the least frequently used ones.
 */
@Component
public class ShowtimeCache extends LfuCache<Showtime> {

  /**
   * Creates a Showtime cache with a predefined capacity of 3.
   * This value can be adjusted as needed.
   */
  public ShowtimeCache() {
    super(3); // Sets the cache size (modifiable)
  }
}
