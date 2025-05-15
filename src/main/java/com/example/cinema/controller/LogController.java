package com.example.cinema.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с лог-файлами приложения.
 * Предоставляет API для чтения и фильтрации логов.
 */
@RestController
@RequestMapping("/api/logs")
@Tag(name = "Log Controller", description = "API для работы с лог-файлами")
public class LogController {

  private static final String LOG_FILE_PATH = "./cinema.log";
  private static final DateTimeFormatter DATE_FORMATTER =
          DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Возвращает логи за указанную дату.
   *
   * @param date дата в формате yyyy-MM-dd
   * @return ResponseEntity с отфильтрованными логами или сообщением об ошибке
   */
  @GetMapping("/by-date")
  @Operation(
          summary = "Получить логи за дату",
          description = "Возвращает логи за указанную дату")
  @ApiResponse(
          responseCode = "200",
          description = "Логи успешно получены")
  @ApiResponse(
          responseCode = "400",
          description = "Неверный формат даты")
  @ApiResponse(
          responseCode = "404",
          description = "Логи не найдены")
  public ResponseEntity<String> getLogsByDate(
          @Parameter(
                  description = "Дата в формате yyyy-MM-dd",
                  example = "2023-10-01")
          @RequestParam String date) {

    try {
      LocalDate.parse(date, DATE_FORMATTER);
    } catch (DateTimeParseException e) {
      return ResponseEntity.badRequest()
              .body("Неверный формат даты. Используйте формат yyyy-MM-dd");
    }

    try {
      Path logPath = Paths.get(LOG_FILE_PATH);
      if (!Files.exists(logPath)) {
        return ResponseEntity.notFound().build();
      }

      String logs;
      try (Stream<String> lines = Files.lines(logPath)) {
        logs = lines
                .filter(line -> line.contains(date))
                .collect(Collectors.joining("\n"));
      }

      if (logs.isEmpty()) {
        return ResponseEntity.notFound().build();
      }

      return ResponseEntity.ok()
              .contentType(MediaType.TEXT_PLAIN)
              .body(logs);
    } catch (IOException e) {
      return ResponseEntity.internalServerError()
              .body("Ошибка при чтении лог-файла");
    }
  }
}