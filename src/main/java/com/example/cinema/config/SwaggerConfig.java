package com.example.cinema.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация Swagger/OpenAPI для документации REST API.
 * Настраивает базовую информацию о API для генерации документации.
 */
@Configuration
public class SwaggerConfig {

  /**
   * Создает конфигурацию OpenAPI с основной информацией о API.
   *
   * @return объект OpenAPI с настройками документации
   */
  @Bean
  public OpenAPI customOpenApi() {
    return new OpenAPI()
            .info(new Info()
                    .title("Cinema API")
                    .version("1.0")
                    .description("API для управления кинотеатром"));
  }
}