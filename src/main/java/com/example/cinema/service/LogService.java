package com.example.cinema.service;

import com.example.cinema.exception.ResourceNotFoundException;
import com.example.cinema.model.LogObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LogService {

  public AtomicLong idCounter = new AtomicLong(1);
  public Map<Long, LogObject> tasks = new ConcurrentHashMap<>();
  public LogService self;
  private String logFilePath = "./cinema.log";

  public LogService(@Lazy LogService self) {
    this.self = self;
  }

  void setLogFilePath(String path) {
    this.logFilePath = path;
  }

  @Async("executor")
  public void createLogs(Long taskId, String date) {
    try {
      Thread.sleep(10000);

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
      LocalDate logDate = LocalDate.parse(date, formatter);

      Path path = Paths.get(this.logFilePath);
      List<String> logLines = Files.readAllLines(path);
      String formattedDate = logDate.format(formatter);
      List<String> currentLogs = logLines.stream()
              .filter(line -> line.startsWith(formattedDate))
              .toList();

      if (currentLogs.isEmpty()) {
        LogObject logObject = tasks.get(taskId);
        if (logObject != null) {
          logObject.setStatus("FAILED");
          logObject.setErrorMessage("Нет логов за дату: " + date);
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Нет логов за дату: " + date);
      }

      Path logFile;
      logFile = Files.createTempFile("logs-" + formattedDate, ".log");

      Files.write(logFile, currentLogs);
      logFile.toFile().deleteOnExit();

      LogObject task = tasks.get(taskId);
      if (task != null) {
        task.setStatus("COMPLETED");
        task.setFilePath(logFile.toString());
      }
    } catch (IOException e) {
      LogObject task = tasks.get(taskId);
      if (task != null) {
        task.setStatus("FAILED");
        task.setErrorMessage(e.getMessage());
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  public Long createLogAsync(String date) {
    Long id = idCounter.getAndIncrement();
    LogObject logObject = new LogObject(id, "IN_PROGRESS");
    tasks.put(id, logObject);
    self.createLogs(id, date);
    return id;
  }

  public LogObject getStatus(Long taskId) {
    return tasks.get(taskId);
  }

  public ResponseEntity<Resource> downloadCreatedLogs(Long taskId) throws IOException {
    LogObject logObject = getStatus(taskId);
    if (logObject == null) {
      throw new ResourceNotFoundException("Не найден log файл");
    }
    if (!"COMPLETED".equals(logObject.getStatus())) {
      //throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Логов ещё нет");
      return ResponseEntity.status(HttpStatus. CONFLICT)
              .header("Lof-Status", logObject.getStatus())
              .build();
    }

    Path path = Paths.get(logObject.getFilePath());
    Resource resource = new UrlResource(path.toUri());

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + resource.getFilename() + "\"")
            .body(resource);
  }
}
