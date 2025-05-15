package com.example.cinema.controller;

import com.example.cinema.exception.ResourceNotFoundException;
import com.example.cinema.model.Hall;
import com.example.cinema.service.HallService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing cinema halls.
 * Provides CRUD operations for hall entities.
 */
@RestController
@RequestMapping("/api/halls")
@Tag(name = "Hall Controller", description = "API for managing cinema halls")
public class HallController {

  private final HallService hallService;

  /**
   * Constructs a HallController with the specified HallService.
   *
   * @param hallService the service to handle hall operations
   */
  public HallController(HallService hallService) {
    this.hallService = hallService;
  }

  /**
   * Retrieves all cinema halls.
   *
   * @return ResponseEntity containing list of all halls
   */
  @GetMapping
  @Operation(summary = "Get all halls", description = "Returns list of all halls")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved")
  public ResponseEntity<List<Hall>> getAllHalls() {
    return ResponseEntity.ok(hallService.getAllHalls());
  }

  /**
   * Creates a new cinema hall.
   *
   * @param hall the hall to create
   * @return ResponseEntity containing the created hall
   */
  @PostMapping
  @Operation(summary = "Create a new hall", description = "Creates a new cinema hall")
  @ApiResponse(responseCode = "201", description = "Hall created",
          content = @Content(schema = @Schema(implementation = Hall.class)))
  @ApiResponse(responseCode = "400", description = "Invalid input")
  public ResponseEntity<Hall> createHall(@Valid @RequestBody Hall hall) {
    return ResponseEntity.status(HttpStatus.CREATED).body(hallService.createHall(hall));
  }

  /**
   * Retrieves a hall by its ID.
   *
   * @param hallId the ID of the hall to retrieve
   * @return ResponseEntity containing the requested hall
   */
  @GetMapping("/{hallId}")
  @Operation(summary = "Get hall by ID", description = "Returns a single hall by its ID")
  @ApiResponse(responseCode = "200", description = "Hall found",
          content = @Content(schema = @Schema(implementation = Hall.class)))
  @ApiResponse(responseCode = "404", description = "Hall not found")
  public ResponseEntity<Hall> getHallById(@PathVariable Long hallId) {
    return ResponseEntity.ok(hallService.getHallById(hallId)
            .orElseThrow(() -> new ResourceNotFoundException("Hall not found with id " + hallId)));
  }

  /**
   * Updates an existing hall.
   *
   * @param hallId the ID of the hall to update
   * @param updatedHall the updated hall data
   * @return ResponseEntity containing the updated hall
   */
  @PutMapping("/{hallId}")
  @Operation(summary = "Update hall", description = "Updates an existing hall")
  @ApiResponse(responseCode = "200", description = "Hall updated",
          content = @Content(schema = @Schema(implementation = Hall.class)))
  @ApiResponse(responseCode = "400", description = "Invalid input")
  @ApiResponse(responseCode = "404", description = "Hall not found")
  public ResponseEntity<Hall> updateHall(
          @PathVariable Long hallId,
          @Valid @RequestBody Hall updatedHall) {
    return ResponseEntity.ok(hallService.updateHall(hallId, updatedHall)
            .orElseThrow(() -> new ResourceNotFoundException("Hall not found with id " + hallId)));
  }

  /**
   * Deletes a hall by its ID.
   *
   * @param hallId the ID of the hall to delete
   * @return ResponseEntity with no content
   */
  @DeleteMapping("/{hallId}")
  @Operation(summary = "Delete hall", description = "Deletes a hall by its ID")
  @ApiResponse(responseCode = "204", description = "Hall deleted")
  @ApiResponse(responseCode = "404", description = "Hall not found")
  public ResponseEntity<Void> deleteHall(@PathVariable Long hallId) {
    hallService.deleteHall(hallId);
    return ResponseEntity.noContent().build();
  }

  /**
   * Creates multiple cinema halls in bulk.
   *
   * @param halls list of halls to create
   * @return ResponseEntity containing list of created halls
   */
  @PostMapping("/bulk")
  @Operation(summary = "Create multiple halls",
          description = "Creates multiple cinema halls in one operation")
  @ApiResponse(responseCode = "201", description = "Halls created",
          content = @Content(schema = @Schema(implementation = Hall.class)))
  @ApiResponse(responseCode = "400", description = "Invalid input")
  public ResponseEntity<List<Hall>> createHallsBulk(@Valid @RequestBody List<Hall> halls) {
    List<Hall> createdHalls = hallService.createHallsBulk(halls);
    return ResponseEntity.status(HttpStatus.CREATED).body(createdHalls);
  }
}