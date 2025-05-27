package com.example.cinema.service;

import com.example.cinema.cache.ShowtimeCache;
import com.example.cinema.model.Hall;
import com.example.cinema.model.Showtime;
import com.example.cinema.repository.HallRepository;
import com.example.cinema.repository.ShowtimeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.eq;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShowtimeServiceTest {

  @Mock
  private ShowtimeRepository showtimeRepository;

  @Mock
  private HallRepository hallRepository;

  @Mock
  private ShowtimeCache showtimeCache;

  @InjectMocks
  private ShowtimeService showtimeService;

  private Showtime showtime;
  private Hall hall;
  private LocalDateTime testDateTime;

  @BeforeEach
  void setUp() {
    testDateTime = LocalDateTime.of(2023, 12, 15, 18, 30);

    hall = new Hall();
    hall.setId(1L);
    hall.setName("Hall A");
    hall.setCapacity(100);

    showtime = new Showtime();
    showtime.setId(1L);
    showtime.setFilmTitle("Inception");
    showtime.setDateTime(testDateTime);
    showtime.setHall(hall);
  }



  @Test
  void createShowtime_hallNotFound() {
    when(hallRepository.findById(1L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> showtimeService.createShowtime(1L, "Inception", testDateTime)
    );

    assertEquals("Hall not found", exception.getMessage());
    verify(showtimeRepository, never()).save(any());
  }

  @Test
  void getShowtimeById_fromCache() {
    when(showtimeCache.get(1L)).thenReturn(showtime);

    Showtime result = showtimeService.getShowtimeById(1L);

    assertNotNull(result);
    assertEquals("Inception", result.getFilmTitle());
    verify(showtimeRepository, never()).findById(any());
  }

  @Test
  void getShowtimeById_fromRepository() {
    when(showtimeCache.get(1L)).thenReturn(null);
    when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));

    Showtime result = showtimeService.getShowtimeById(1L);

    assertNotNull(result);
    assertEquals("Inception", result.getFilmTitle());
    verify(showtimeCache).put(1L, showtime);
  }

  @Test
  void getShowtimeById_notFound() {
    when(showtimeCache.get(1L)).thenReturn(null);
    when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> showtimeService.getShowtimeById(1L)
    );

    assertEquals("Showtime not found", exception.getMessage());
  }

  @Test
  void updateShowtime_success() {
    Showtime updated = new Showtime();
    updated.setFilmTitle("Interstellar");
    updated.setDateTime(testDateTime.plusDays(1));
    updated.setHall(hall);

    when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
    when(hallRepository.findById(1L)).thenReturn(Optional.of(hall));
    when(showtimeRepository.save(any(Showtime.class))).thenReturn(updated);

    Showtime result = showtimeService.updateShowtime(1L, updated);

    assertNotNull(result);
    assertEquals("Interstellar", result.getFilmTitle());
    assertEquals(testDateTime.plusDays(1), result.getDateTime());

    // Используем ArgumentCaptor для проверки put в кэш
    ArgumentCaptor<Showtime> showtimeCaptor = ArgumentCaptor.forClass(Showtime.class);
    verify(showtimeCache).put(eq(1L), showtimeCaptor.capture());

    Showtime cachedShowtime = showtimeCaptor.getValue();
    assertEquals("Interstellar", cachedShowtime.getFilmTitle());
    assertEquals(testDateTime.plusDays(1), cachedShowtime.getDateTime());
  }

  @Test
  void updateShowtime_showtimeNotFound() {
    Showtime updated = new Showtime();
    when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> showtimeService.updateShowtime(1L, updated)
    );

    assertEquals("Showtime not found", exception.getMessage());
  }

  @Test
  void updateShowtime_hallNotFound() {
    Showtime updated = new Showtime();
    updated.setHall(hall);

    when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));
    when(hallRepository.findById(1L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> showtimeService.updateShowtime(1L, updated)
    );

    assertEquals("Hall not found", exception.getMessage());
  }

  @Test
  void deleteShowtime_success() {
    when(showtimeRepository.findById(1L)).thenReturn(Optional.of(showtime));

    showtimeService.deleteShowtime(1L);

    verify(showtimeRepository).delete(showtime);
    verify(showtimeCache).remove(1L);
  }

  @Test
  void deleteShowtime_notFound() {
    when(showtimeRepository.findById(1L)).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> showtimeService.deleteShowtime(1L)
    );

    assertEquals("Showtime not found", exception.getMessage());
    verify(showtimeRepository, never()).delete(any());
  }

  @Test
  void filterByTitle_emptyResult() {
    when(showtimeRepository.findByHallIdAndFilmTitleContainingIgnoreCase(1L, "Nonexistent"))
            .thenReturn(Collections.emptyList());

    List<Showtime> result = showtimeService.filterByTitle(1L, "Nonexistent");

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void filterByTitle_success() {
    when(showtimeRepository.findByHallIdAndFilmTitleContainingIgnoreCase(1L, "Incep"))
            .thenReturn(List.of(showtime));

    List<Showtime> result = showtimeService.filterByTitle(1L, "Incep");

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Inception", result.get(0).getFilmTitle());
  }

  @Test
  void filterByDate_emptyResult() {
    LocalDate testDate = testDateTime.toLocalDate();
    when(showtimeRepository.findByHallIdAndDate(1L, testDate))
            .thenReturn(Collections.emptyList());

    List<Showtime> result = showtimeService.filterByDate(1L, testDate);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void filterByDate_success() {
    LocalDate testDate = testDateTime.toLocalDate();
    when(showtimeRepository.findByHallIdAndDate(1L, testDate))
            .thenReturn(List.of(showtime));

    List<Showtime> result = showtimeService.filterByDate(1L, testDate);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(testDateTime, result.get(0).getDateTime());
  }
}