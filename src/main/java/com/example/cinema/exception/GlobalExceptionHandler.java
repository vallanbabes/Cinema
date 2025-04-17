package com.example.cinema.exception;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * Глобальный обработчик исключений для REST API.
 * Перехватывает и обрабатывает исключения, возникающие в контроллерах.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Обрабатывает исключения валидации входных данных.
   *
   * @param ex исключение MethodArgumentNotValidException
   * @return ResponseEntity с картой ошибок валидации
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
          MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    logger.error("Ошибка валидации: {}", errors);
    return ResponseEntity.badRequest().body(errors);
  }

  /**
   * Обрабатывает кастомные исключения валидации.
   *
   * @param ex исключение ValidationException
   * @return ResponseEntity с сообщением об ошибке
   */
  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<String> handleValidationException(ValidationException ex) {
    logger.error("Ошибка валидации: {}", ex.getMessage());
    return ResponseEntity.badRequest().body(ex.getMessage());
  }

  /**
   * Обрабатывает исключения при отсутствии ресурса.
   *
   * @param ex исключение ResourceNotFoundException
   * @return ResponseEntity с сообщением об ошибке и статусом 404
   */
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
    logger.error("Ресурс не найден: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  /**
   * Обрабатывает все неперехваченные исключения.
   *
   * @param ex исключение
   * @return ResponseEntity с общим сообщением об ошибке и статусом 500
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGenericException(Exception ex) {
    logger.error("Внутренняя ошибка сервера: {}", ex.getMessage(), ex);
    return ResponseEntity.internalServerError()
            .body("Произошла внутренняя ошибка сервера: " + ex.getMessage());
  }
}