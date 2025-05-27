package com.example.cinema.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor // Генерирует конструктор без аргументов
@AllArgsConstructor // Генерирует конструктор со всеми полями
public class ShowtimeDto {
  private Long id;
  private LocalDateTime dateTime;
  private String filmTitle;
  private HallDto hall; // Создайте аналогичный HallDto
}
