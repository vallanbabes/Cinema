package com.example.cinema.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Аспект для логирования вызовов методов в контроллерах.
 * Обеспечивает логирование перед выполнением, после успешного выполнения
 * и при возникновении исключений в методах контроллеров.
 */
@Aspect
@Component
public class LoggingAspect {

  private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

  /**
   * Логирует вызов метода перед его выполнением.
   *
   * @param joinPoint точка соединения, содержащая информацию о вызываемом методе
   */
  @Before("execution(* com.example.cinema.controller.*.*(..))")
  public void logBefore(JoinPoint joinPoint) {
    logger.info("Выполнение метода: {}", joinPoint.getSignature().toShortString());
  }

  /**
   * Логирует успешное выполнение метода.
   *
   * @param joinPoint точка соединения, содержащая информацию о вызываемом методе
   * @param result выполнения метода
   */
  @AfterReturning(
          pointcut = "execution(* com.example.cinema.controller.*.*(..))",
          returning = "result")
  public void logAfterReturning(JoinPoint joinPoint, Object result) {
    logger.info(
            "Метод {} успешно выполнен. Результат: {}",
            joinPoint.getSignature().toShortString(),
            result != null ? result.toString() : "null");
  }

  /**
   * Логирует исключение, возникшее при выполнении метода.
   *
   * @param joinPoint точка соединения, содержащая информацию о вызываемом методе
   * @param error исключение, которое было выброшено
   */
  @AfterThrowing(
          pointcut = "execution(* com.example.cinema.controller.*.*(..))",
          throwing = "error")
  public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
    logger.error(
            "Ошибка в методе: {}. Сообщение: {}",
            joinPoint.getSignature().toShortString(),
            error.getMessage());
  }
}