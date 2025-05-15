package com.example.cinema.service;

import com.example.cinema.exception.ResourceNotFoundException;
import com.example.cinema.model.Hall;
import com.example.cinema.repository.HallRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Service class for managing cinema halls.
 * Provides business logic for hall-related operations.
 */
@Service
public class HallService {

  private final HallRepository hallRepository;
  private final VisitCounterService visitCounterService;

  /**
   * Constructs a HallService with the specified HallRepository.
   *
   * @param hallRepository the repository for hall data access
   */
  public HallService(HallRepository hallRepository,
                     VisitCounterService visitCounterService) {
    this.hallRepository = hallRepository;
    this.visitCounterService = visitCounterService;
  }

  /**
   * Retrieves all cinema halls.
   *
   * @return list of all halls
   */
  public List<Hall> getAllHalls() {
    visitCounterService.increment();
    return hallRepository.findAll();
  }

  /**
   * Creates a new cinema hall.
   *
   * @param hall the hall to create
   * @return the created hall
   */
  public Hall createHall(Hall hall) {
    return hallRepository.save(hall);
  }

  /**
   * Retrieves a hall by its ID.
   *
   * @param hallId the ID of the hall to retrieve
   * @return Optional containing the hall if found
   */
  public Optional<Hall> getHallById(Long hallId) {
    return hallRepository.findById(hallId);
  }

  /**
   * Updates an existing hall.
   *
   * @param hallId the ID of the hall to update
   * @param updatedHall the updated hall data
   * @return Optional containing the updated hall if found
   */
  public Optional<Hall> updateHall(Long hallId, Hall updatedHall) {
    return hallRepository.findById(hallId).map(existingHall -> {
      existingHall.setName(updatedHall.getName());
      existingHall.setCapacity(updatedHall.getCapacity());
      return hallRepository.save(existingHall);
    });
  }

  /**
   * Deletes a hall by its ID.
   *
   * @param hallId the ID of the hall to delete
   * @throws ResourceNotFoundException if hall is not found
   */
  public void deleteHall(Long hallId) {
    if (!hallRepository.existsById(hallId)) {
      throw new ResourceNotFoundException("Hall not found with id " + hallId);
    }
    hallRepository.deleteById(hallId);
  }

  /**
   * Creates multiple cinema halls in bulk.
   *
   * @param halls list of halls to create
   * @return list of created halls
   */
  public List<Hall> createHallsBulk(List<Hall> halls) {
    return halls.stream()
            .map(this::createHall)
            .toList();
  }
}