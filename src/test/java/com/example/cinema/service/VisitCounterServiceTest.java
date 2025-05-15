package com.example.cinema.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class VisitCounterServiceTest {

  private VisitCounterService visitCounterService;

  @BeforeEach
  void setUp() {
    visitCounterService = new VisitCounterService();
  }

  @Test
  void increment_shouldIncreaseCounterByOne() {
    // Initial state
    assertEquals(0L, visitCounterService.getCounter());

    // First increment
    visitCounterService.increment();
    assertEquals(1L, visitCounterService.getCounter());

    // Second increment
    visitCounterService.increment();
    assertEquals(2L, visitCounterService.getCounter());
  }

  @Test
  void getCounter_shouldReturnCurrentValue() {
    assertEquals(0L, visitCounterService.getCounter());

    visitCounterService.increment();
    assertEquals(1L, visitCounterService.getCounter());

    visitCounterService.increment();
    visitCounterService.increment();
    assertEquals(3L, visitCounterService.getCounter());
  }

  @Test
  void increment_fromMultipleThreads_shouldMaintainCorrectCount() throws InterruptedException {
    final int threadCount = 100;
    assertEquals(0L, visitCounterService.getCounter());

    // Create and start multiple threads
    Thread[] threads = new Thread[threadCount];
    for (int i = 0; i < threadCount; i++) {
      threads[i] = new Thread(() -> {
        visitCounterService.increment();
      });
      threads[i].start();
    }

    // Wait for all threads to complete
    for (Thread thread : threads) {
      thread.join();
    }

    assertEquals(threadCount, visitCounterService.getCounter());
  }

  @Test
  void getCounter_withoutIncrements_shouldReturnZero() {
    assertEquals(0L, visitCounterService.getCounter());
  }
}