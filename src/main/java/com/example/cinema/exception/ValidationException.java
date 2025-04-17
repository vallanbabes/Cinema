package com.example.cinema.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Исключение для обработки ошибок валидации.
 * Автоматически возвращает HTTP статус 400 (Bad Request) при возникновении.
 */
public class ValidationException extends ResponseStatusException {

  /**
   * Создает исключение с указанным сообщением об ошибке.
   *
   * @param reason сообщение об ошибке валидации
   */
  public ValidationException(String reason) {
    super(HttpStatus.BAD_REQUEST, reason);
  }
}