package com.example.cinema.controller;

import com.example.cinema.model.LogObject;
import com.example.cinema.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")

@Tag(name = "Логи", description = "Управление логами")
public class LogController {

  private final LogService logService;

  public LogController(LogService logService) {
    this.logService = logService;
  }

  @PostMapping("/create")
  @Operation(
          summary = "Создать лог-файл асинхронно",
          description = "Запускает генерацию лог-файла и возвращает его ID"
  )
  public ResponseEntity<Long> createLogFile(@RequestParam String date) {
    Long id = logService.createLogAsync(date);
    return ResponseEntity.ok(id);
  }

  @GetMapping("/status/{id}")
  @Operation(
          summary = "Получить состояние создания лог-файла",
          description = "Возвращает состояние создания лог-файла по ID"
  )
  public ResponseEntity<Map<String, String>> getStatus(@PathVariable Long id) {
    LogObject logObject = logService.getStatus(id);
    Map<String, String> response = new HashMap<>();
    response.put("status", logObject.getStatus());
    if (logObject.getErrorMessage() != null) {
      response.put("error", logObject.getErrorMessage());
    }
    return ResponseEntity.ok(response);
  }

  @GetMapping("/download/{id}")
  @Operation(
          summary = "Скачать созданный лог-файл",
          description = "Скачивает созданный лог-файл по ID"
  )
  public ResponseEntity<Resource> getLogFileById(@PathVariable Long id) throws IOException {
    return logService.downloadCreatedLogs(id);
  }
}