package com.example.cinema.service;

import com.example.cinema.model.Hall;
import com.example.cinema.repository.HallRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service class for managing cinema halls.
 */
@Service
public class HallService {

  private final HallRepository hallRepository;

  /**
   * Constructs a HallService with the specified HallRepository.
   *
   * @param hallRepository the repository used for hall operations
   */
  public HallService(HallRepository hallRepository) {
    this.hallRepository = hallRepository;
  }

  /**
   * Retrieves all halls.
   *
   * @return a list of all halls
   */
  public List<Hall> getAllHalls() {
    return hallRepository.findAll();
  }

  /**
   * Creates a new hall.
   *
   * @param hall the hall to create
   * @return the created hall
   */
  public Hall createHall(Hall hall) {
    return hallRepository.save(hall);
  }

  /**
   * Deletes a hall by its ID.
   *
   * @param hallId the ID of the hall to delete
   * @throws ResponseStatusException if the hall is not found
   */
  public void deleteHall(Long hallId) {
    Hall hall = hallRepository.findById(hallId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall not found"));

    hallRepository.delete(hall);
  }

  /**
   * Retrieves a hall by its ID.
   *
   * @param hallId the ID of the hall to retrieve
   * @return the requested hall
   * @throws ResponseStatusException if the hall is not found
   */
  public Hall getHallById(Long hallId) {
    return hallRepository.findById(hallId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall not found"));
  }

  /**
   * Updates information about a hall.
   *
   * @param hallId the ID of the hall to update
   * @param updatedHall the updated hall details
   * @return the updated hall
   * @throws ResponseStatusException if the hall is not found
   */
  public Hall updateHall(Long hallId, Hall updatedHall) {
    Hall existingHall = hallRepository.findById(hallId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Hall not found"));

    existingHall.setName(updatedHall.getName());

    return hallRepository.save(existingHall);
  }
}