package com.example.cinema.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.Signature;
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
   * Получает короткое имя метода из точки соединения.
   * Защищенный метод для обработки возможных NPE.
   *
   * @param joinPoint точка соединения
   * @return короткое имя метода или "unknown" если не удалось получить
   */
  private String getShortMethodName(JoinPoint joinPoint) {
    if (joinPoint == null) {
      return "unknown";
    }
    Signature signature = joinPoint.getSignature();
    return signature != null ? signature.toShortString() : "unknown";
  }

  /**
   * Логирует вызов метода перед его выполнением.
   *
   * @param joinPoint точка соединения, содержащая информацию о вызываемом методе
   */
  @Before("execution(* com.example.cinema.controller.*.*(..))")
  public void logBefore(JoinPoint joinPoint) {
    String methodName = getShortMethodName(joinPoint);
    logger.info("Выполнение метода: {}", methodName);
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
    String methodName = getShortMethodName(joinPoint);
    String resultString = result != null ? result.toString() : "null";
    logger.info("Метод {} успешно выполнен. Результат: {}", methodName, resultString);
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
    String methodName = getShortMethodName(joinPoint);
    String errorMsg = error != null ? error.getMessage() : "unknown error";
    logger.error("Ошибка в методе: {}. Сообщение: {}", methodName, errorMsg);
  }
}