package com.example.cinema.controller;

import com.example.cinema.model.Hall;
import com.example.cinema.service.HallService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing cinema halls.
 */
@RestController
@RequestMapping("/api/halls")
public class HallController {

  private final HallService hallService;

  /**
   * Constructor to initialize HallController with HallService.
   *
   * @param hallService service for handling hall operations
   */
  public HallController(HallService hallService) {
    this.hallService = hallService;
  }

  /**
   * Gets all halls.
   *
   * @return list of all halls
   */
  @GetMapping
  public List<Hall> getAllHalls() {
    return hallService.getAllHalls();
  }

  /**
   * Creates a new hall.
   *
   * @param hall the hall to create
   * @return the created hall
   */
  @PostMapping
  public Hall createHall(@RequestBody Hall hall) {
    return hallService.createHall(hall);
  }

  /**
   * Deletes a hall by ID.
   *
   * @param hallId ID of the hall to delete
   */
  @DeleteMapping("/{hallId}")
  public void deleteHall(@PathVariable Long hallId) {
    hallService.deleteHall(hallId);
  }

  /**
   * Gets a hall by ID.
   *
   * @param hallId ID of the hall to retrieve
   * @return the requested hall
   */
  @GetMapping("/{hallId}")
  public Hall getHallById(@PathVariable Long hallId) {
    return hallService.getHallById(hallId);
  }

  /**
   * Updates information about a hall.
   *
   * @param hallId ID of the hall to update
   * @param updatedHall updated hall details
   * @return the updated hall
   */
  @PutMapping("/{hallId}")
  public Hall updateHall(@PathVariable Long hallId, @RequestBody Hall updatedHall) {
    return hallService.updateHall(hallId, updatedHall);
  }
}