package com.example.cinema.service;

import com.example.cinema.exception.ResourceNotFoundException;
import com.example.cinema.model.Hall;
import com.example.cinema.repository.HallRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HallServiceTest {

  @Mock
  private HallRepository hallRepository;

  @InjectMocks
  private HallService hallService;

  private Hall hall;
  private Hall updatedHall;

  @BeforeEach
  void setUp() {
    hall = new Hall();
    hall.setId(1L);
    hall.setName("Hall A");
    hall.setCapacity(100);

    updatedHall = new Hall();
    updatedHall.setName("Hall B");
    updatedHall.setCapacity(150);
  }

  @Test
  void getAllHalls_success() {
    when(hallRepository.findAll()).thenReturn(List.of(hall));

    List<Hall> result = hallService.getAllHalls();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Hall A", result.get(0).getName());
  }

  @Test
  void getAllHalls_emptyList() {
    when(hallRepository.findAll()).thenReturn(List.of());

    List<Hall> result = hallService.getAllHalls();

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void createHall_success() {
    when(hallRepository.save(any(Hall.class))).thenReturn(hall);

    Hall result = hallService.createHall(hall);

    assertNotNull(result);
    assertEquals("Hall A", result.getName());
    assertEquals(100, result.getCapacity());
  }

  @Test
  void getHallById_success() {
    when(hallRepository.findById(1L)).thenReturn(Optional.of(hall));

    Optional<Hall> result = hallService.getHallById(1L);

    assertTrue(result.isPresent());
    assertEquals("Hall A", result.get().getName());
  }

  @Test
  void getHallById_notFound() {
    when(hallRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<Hall> result = hallService.getHallById(1L);

    assertTrue(result.isEmpty());
  }

  @Test
  void updateHall_success() {
    when(hallRepository.findById(1L)).thenReturn(Optional.of(hall));
    when(hallRepository.save(any(Hall.class))).thenReturn(hall);

    Optional<Hall> result = hallService.updateHall(1L, updatedHall);

    assertTrue(result.isPresent());
    assertEquals("Hall B", result.get().getName());
    assertEquals(150, result.get().getCapacity());
  }

  @Test
  void updateHall_notFound() {
    when(hallRepository.findById(1L)).thenReturn(Optional.empty());

    Optional<Hall> result = hallService.updateHall(1L, updatedHall);

    assertTrue(result.isEmpty());
  }

  @Test
  void deleteHall_success() {
    when(hallRepository.existsById(1L)).thenReturn(true);

    hallService.deleteHall(1L);

    verify(hallRepository).deleteById(1L);
  }

  @Test
  void deleteHall_notFound() {
    when(hallRepository.existsById(1L)).thenReturn(false);

    assertThrows(ResourceNotFoundException.class, () -> hallService.deleteHall(1L));

    verify(hallRepository, never()).deleteById(anyLong());
  }

  @Test
  void createHallsBulk_success() {
    Hall hall2 = new Hall();
    hall2.setName("Hall B");
    hall2.setCapacity(200);

    List<Hall> hallsToCreate = Arrays.asList(hall, hall2);
    when(hallRepository.save(any(Hall.class)))
            .thenReturn(hall)
            .thenReturn(hall2);

    List<Hall> result = hallService.createHallsBulk(hallsToCreate);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Hall A", result.get(0).getName());
    assertEquals("Hall B", result.get(1).getName());
    verify(hallRepository, times(2)).save(any(Hall.class));
  }

  @Test
  void createHallsBulk_emptyList() {
    List<Hall> result = hallService.createHallsBulk(List.of());

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(hallRepository, never()).save(any(Hall.class));
  }
}