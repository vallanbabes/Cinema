package com.example.cinema.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Исключение, выбрасываемое когда запрашиваемый ресурс не найден.
 * Автоматически возвращает HTTP статус 404 (Not Found) при возникновении.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

  /**
   * Создает новое исключение с указанным сообщением об ошибке.
   *
   * @param message детальное сообщение об ошибке
   */
  public ResourceNotFoundException(final String message) {
    super(message);
  }
}