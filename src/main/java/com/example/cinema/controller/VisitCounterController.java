package com.example.cinema.controller;

import com.example.cinema.service.VisitCounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class VisitCounterController {

  private final VisitCounterService visitCounterService;

  VisitCounterController(VisitCounterService visitCounterService) {
    this.visitCounterService = visitCounterService;
  }

  @GetMapping("/count")
  public ResponseEntity<Long> getCount() {
    Long count = visitCounterService.getCounter();
    return ResponseEntity.ok(count);
  }
}