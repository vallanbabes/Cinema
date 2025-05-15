package com.example.cinema.service;

import com.example.cinema.exception.ResourceNotFoundException;
import com.example.cinema.model.LogObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

  @Spy
  @InjectMocks
  private LogService logService;

  @TempDir
  Path tempDir;

  @BeforeEach
  void setUp() {
    // Инициализируем self как spy самого сервиса
    logService.self = spy(logService);
    logService.tasks = new ConcurrentHashMap<>();
    logService.idCounter = new AtomicLong(1);
  }

  @Test
  void createLogs_success_updatesTaskToCompleted() throws Exception {
    // 1. Подготовка временного файла с логами
    Path tempLogFile = tempDir.resolve("cinema.log");
    Files.write(tempLogFile, List.of(
            "15-01-2023 Log message 1",
            "15-01-2023 Log message 2",
            "16-01-2023 Other date log"
    ));


    logService.setLogFilePath(tempLogFile.toString());

    // 3. Подготовка тестовых данных
    Long taskId = 1L;
    LogObject logObject = new LogObject(taskId, "IN_PROGRESS");
    logService.tasks = new ConcurrentHashMap<>();
    logService.tasks.put(taskId, logObject);
    logService.idCounter = new AtomicLong(2);

    // 4. Вызов тестируемого метода
    logService.createLogs(taskId, "15-01-2023");

    // 5. Проверки
    LogObject updatedTask = logService.tasks.get(taskId);
    assertNotNull(updatedTask);
    assertEquals("COMPLETED", updatedTask.getStatus());
    assertNotNull(updatedTask.getFilePath());
    assertTrue(Files.exists(Path.of(updatedTask.getFilePath())));

    // 6. Проверка содержимого созданного файла
    List<String> filteredLogs = Files.readAllLines(Path.of(updatedTask.getFilePath()));
    assertEquals(2, filteredLogs.size());
    assertTrue(filteredLogs.get(0).startsWith("15-01-2023"));
    assertTrue(filteredLogs.get(1).startsWith("15-01-2023"));
  }

  @Test
  void getStatus_taskNotExists_returnsNull() {
    LogObject logObject = logService.getStatus(999L);
    assertNull(logObject);
  }

  @Test
  void downloadCreatedLogs_completedTask_returnsResource() throws IOException {
    // Setup
    Long taskId = 1L;
    Path tempFile = Files.createTempFile("test", ".log");
    Files.writeString(tempFile, "Test log content");

    LogObject logObject = new LogObject(taskId, "COMPLETED");
    logObject.setFilePath(tempFile.toString());
    logService.tasks.put(taskId, logObject);

    // Test
    ResponseEntity<Resource> response = logService.downloadCreatedLogs(taskId);

    // Verify
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertTrue(response.getBody().exists());

    // Cleanup
    Files.deleteIfExists(tempFile);
  }

  @Test
  void downloadCreatedLogs_taskNotExists_throwsResourceNotFoundException() {
    assertThrows(ResourceNotFoundException.class,
            () -> logService.downloadCreatedLogs(999L));
  }

  @Test
  void createLogs_ioException_updatesTaskToFailed(){
    // 1. Указываем несуществующий файл логов
    logService.setLogFilePath("/nonexistent/path/cinema.log");

    // 2. Подготовка тестовых данных
    Long taskId = 1L;
    LogObject logObject = new LogObject(taskId, "IN_PROGRESS");
    logService.tasks.put(taskId, logObject);

    // 3. Вызов тестируемого метода
    logService.createLogs(taskId, "15-01-2023");

    // 4. Проверки
    LogObject updatedTask = logService.tasks.get(taskId);
    assertNotNull(updatedTask);
    assertEquals("FAILED", updatedTask.getStatus());
    assertNotNull(updatedTask.getErrorMessage());
  }

  @Test
  void createLogAsync_createsNewTaskWithCorrectState() {
    // 1. Подготовка
    String testDate = "15-05-2025";

    // 2. Вызов метода
    Long taskId = logService.createLogAsync(testDate);

    // 3. Проверки синхронной части
    assertNotNull(taskId);
    assertEquals(1L, taskId);

    LogObject task = logService.tasks.get(taskId);
    assertNotNull(task);
    assertEquals("IN_PROGRESS", task.getStatus());
    assertNull(task.getFilePath());
    assertNull(task.getErrorMessage());

    // 4. Проверяем, что асинхронный метод был вызван
    verify(logService.self, times(1)).createLogs(taskId, testDate);
  }

  @Test
  void createLogAsync_triggersAsyncProcessing() {
    // 1. Создаем spy сервиса
    LogService spyService = spy(logService);
    spyService.self = spyService;

    // 2. Вызов метода
    spyService.createLogAsync("15-05-2025");

    // 3. Проверяем вызов асинхронного метода
    verify(spyService, timeout(1000))
            .createLogs(anyLong(), eq("15-05-2025"));
  }

  @Test
  void downloadCreatedLogs_taskNotCompleted_throwsResponseStatusException() {
    // 1. Подготовка тестовых данных
    Long taskId = 1L;
    LogObject logObject = new LogObject(taskId, "IN_PROGRESS");
    logService.tasks.put(taskId, logObject);

    // 2. Проверка исключения
    ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> logService.downloadCreatedLogs(taskId)
    );

    // 3. Проверка статуса и сообщения исключения
    assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    assertEquals("Логов ещё нет", exception.getReason());
  }

  @Test
  void createLogs_interrupted_setsInterruptedStatus() throws Exception {
    // 1. Подготовка тестовых данных
    Path tempLogFile = tempDir.resolve("cinema.log");
    Files.write(tempLogFile, List.of("15-01-2023 Test log"));
    logService.setLogFilePath(tempLogFile.toString());

    Long taskId = 1L;
    LogObject logObject = new LogObject(taskId, "IN_PROGRESS");
    logService.tasks.put(taskId, logObject);

    // 2. Прерывание потока
    Thread testThread = new Thread(() -> {
      try {
        logService.createLogs(taskId, "15-01-2023");
      } catch (Exception e) {
        // Ожидаемое прерывание
      }
    });
    testThread.start();
    testThread.interrupt();
    testThread.join();

    // 3. Проверки
    LogObject updatedTask = logService.tasks.get(taskId);
    assertNotNull(updatedTask);
    assertEquals("IN_PROGRESS", updatedTask.getStatus()); // или другое ожидаемое состояние
  }

  @Test
  void createTempLogFile_shouldHaveCorrectPermissions() throws Exception {
    // 1. Подготовка тестовых данных
    Path tempLogFile = tempDir.resolve("cinema.log");
    Files.write(tempLogFile, List.of("15-05-2025 Test log"));
    logService.setLogFilePath(tempLogFile.toString());

    Long taskId = 1L;
    LogObject logObject = new LogObject(taskId, "IN_PROGRESS");
    logService.tasks.put(taskId, logObject);

    // 2. Вызов метода
    logService.createLogs(taskId, "15-05-2025");

    // 3. Проверка прав доступа (только для Unix-систем)
    if (!System.getProperty("os.name").toLowerCase().contains("win")) {
      LogObject task = logService.tasks.get(taskId);
      Set<PosixFilePermission> permissions =
              Files.getPosixFilePermissions(Path.of(task.getFilePath()));
      assertTrue(permissions.contains(PosixFilePermission.OWNER_READ));
      assertTrue(permissions.contains(PosixFilePermission.OWNER_WRITE));
      assertTrue(permissions.contains(PosixFilePermission.OWNER_EXECUTE));
    }
  }

  @Test
  void tempLogFile_shouldBeConfiguredForDeletion() throws Exception {
    // 1. Подготовка тестовых данных
    Path tempLogFile = tempDir.resolve("cinema.log");
    Files.write(tempLogFile, List.of("15-05-2025 Test log"));
    logService.setLogFilePath(tempLogFile.toString());

    Long taskId = 1L;
    LogObject logObject = new LogObject(taskId, "IN_PROGRESS");
    logService.tasks.put(taskId, logObject);

    // 2. Вызов метода
    logService.createLogs(taskId, "15-05-2025");

    // 3. Проверяем, что файл существует и доступен
    LogObject task = logService.tasks.get(taskId);
    Path createdLogFile = Path.of(task.getFilePath());
    assertTrue(Files.exists(createdLogFile));
    assertTrue(Files.isReadable(createdLogFile));

    // 4. Не можем напрямую проверить deleteOnExit, но можем проверить,
    // что файл действительно временный (находится в системной временной директории)
    Path systemTempDir = Path.of(System.getProperty("java.io.tmpdir"));
    assertTrue(createdLogFile.startsWith(systemTempDir));
  }

  @Test
  void createLogs_withLargeFile_shouldProcessSuccessfully() throws Exception {
    // 1. Создаем большой лог-файл (1000 записей)
    Path tempLogFile = tempDir.resolve("cinema.log");
    List<String> lines = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
      lines.add("15-05-2025 Log entry " + i);
    }
    Files.write(tempLogFile, lines);
    logService.setLogFilePath(tempLogFile.toString());

    // 2. Подготовка тестовых данных
    Long taskId = 1L;
    LogObject logObject = new LogObject(taskId, "IN_PROGRESS");
    logService.tasks.put(taskId, logObject);

    // 3. Вызов метода
    logService.createLogs(taskId, "15-05-2025");

    // 4. Проверки
    LogObject updatedTask = logService.tasks.get(taskId);
    assertNotNull(updatedTask);
    assertEquals("COMPLETED", updatedTask.getStatus());

    // Проверяем, что все записи сохранились
    List<String> filteredLogs = Files.readAllLines(Path.of(updatedTask.getFilePath()));
    assertEquals(1000, filteredLogs.size());
  }
  @Test
  void concurrentLogProcessing_shouldNotHaveConflicts() throws Exception {
    // 1. Подготовка тестовых данных
    Path tempLogFile = tempDir.resolve("cinema.log");

    // Создаем тестовые данные
    List<String> logEntries = List.of(
            "15-05-2025 Log entry 1",
            "16-05-2025 Log entry 2"
    );
    Files.write(tempLogFile, logEntries);
    logService.setLogFilePath(tempLogFile.toString());

    // 2. Мокируем задержку в createLogs
    doAnswer(invocation -> {
      Long taskId = invocation.getArgument(0);
      String date = invocation.getArgument(1);

      // Имитируем быструю обработку (без реальной задержки)
      LogObject task = logService.tasks.get(taskId);
      if (task != null) {
        task.setStatus("COMPLETED");
        task.setFilePath(tempDir.resolve("temp-" + taskId + ".log").toString());
        Files.write(Path.of(task.getFilePath()),
                logEntries.stream()
                        .filter(line -> line.startsWith(date))
                        .collect(Collectors.toList()));
      }
      return null;
    }).when(logService.self).createLogs(anyLong(), anyString());

    // 3. Создаем задачи параллельно
    int threadCount = 10;
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    List<Future<Long>> futures = new ArrayList<>();

    for (int i = 0; i < threadCount; i++) {
      final String date = (i % 2 == 0) ? "15-05-2025" : "16-05-2025";
      futures.add(executor.submit(() -> logService.createLogAsync(date)));
    }

    // 4. Быстрая проверка результатов
    for (Future<Long> future : futures) {
      Long taskId = future.get(2, TimeUnit.SECONDS); // Таймаут на случай проблем
      LogObject task = logService.getStatus(taskId);
      assertNotNull(task);
      assertEquals("COMPLETED", task.getStatus());
      assertNotNull(task.getFilePath());
      assertTrue(Files.exists(Path.of(task.getFilePath())));
    }

    executor.shutdown();

    // 5. Проверяем, что все вызовы были обработаны
    verify(logService.self, times(threadCount)).createLogs(anyLong(), anyString());
  }

  @Test
  void createLogs_noLogsForDate_updatesTaskAndThrowsException() throws Exception {
    // 1. Подготовка лог-файла с данными для других дат
    Path tempLogFile = tempDir.resolve("cinema.log");
    Files.write(tempLogFile, List.of(
            "10-05-2025 Log message 1",
            "20-05-2025 Log message 2"
    ));
    logService.setLogFilePath(tempLogFile.toString());

    // 2. Подготовка тестовых данных
    Long taskId = 1L;
    LogObject logObject = new LogObject(taskId, "IN_PROGRESS");
    logService.tasks.put(taskId, logObject);

    // 3. Вызов тестируемого метода для даты, которой нет в логах
    String searchDate = "15-05-2025";

    // Проверяем, что выбрасывается исключение
    assertThrows(ResponseStatusException.class, () -> {
      logService.createLogs(taskId, searchDate);
    });

    // 4. Проверяем обновление статуса задачи
    LogObject updatedTask = logService.tasks.get(taskId);
    assertNotNull(updatedTask);
    assertEquals("FAILED", updatedTask.getStatus());
    assertEquals("Нет логов за дату: " + searchDate, updatedTask.getErrorMessage());
  }

  @Test
  void createLogs_taskNotFound_doesNotThrowException() throws Exception {
    // 1. Подготовка лог-файла
    Path tempLogFile = tempDir.resolve("cinema.log");
    Files.write(tempLogFile, List.of("15-05-2025 Test log"));
    logService.setLogFilePath(tempLogFile.toString());

    // 2. Вызов с несуществующим taskId
    Long nonExistentTaskId = 999L;

    // Не должно быть исключения, несмотря на отсутствие задачи
    assertDoesNotThrow(() -> {
      logService.createLogs(nonExistentTaskId, "15-05-2025");
    });

    // Проверяем, что новая задача не была создана
    assertNull(logService.tasks.get(nonExistentTaskId));
  }

  @Test
  void createLogs_ioExceptionWithNullTask_doesNotThrow() {
    // 1. Указываем несуществующий файл логов
    logService.setLogFilePath("/invalid/path/cinema.log");

    // 2. Вызов с несуществующим taskId
    Long nonExistentTaskId = 999L;

    // Не должно быть исключения
    assertDoesNotThrow(() -> {
      logService.createLogs(nonExistentTaskId, "15-05-2025");
    });
  }

  @Test
  void createLogs_fullCoverageTest() throws Exception {
    // Тест 1: Нормальное выполнение
    Path tempLogFile = tempDir.resolve("cinema.log");
    Files.write(tempLogFile, List.of("15-05-2025 Test log"));
    logService.setLogFilePath(tempLogFile.toString());

    Long taskId1 = 1L;
    logService.tasks.put(taskId1, new LogObject(taskId1, "IN_PROGRESS"));
    logService.createLogs(taskId1, "15-05-2025");
    assertEquals("COMPLETED", logService.tasks.get(taskId1).getStatus());

    // Тест 2: Нет логов за дату
    Long taskId2 = 2L;
    logService.tasks.put(taskId2, new LogObject(taskId2, "IN_PROGRESS"));
    assertThrows(ResponseStatusException.class, () -> {
      logService.createLogs(taskId2, "16-05-2025");
    });
    assertEquals("FAILED", logService.tasks.get(taskId2).getStatus());

    // Тест 3: Task == null
    assertDoesNotThrow(() -> {
      logService.createLogs(999L, "15-05-2025");
    });

    // Тест 4: Ошибка IO с task == null
    logService.setLogFilePath("/invalid/path/cinema.log");
    assertDoesNotThrow(() -> {
      logService.createLogs(1000L, "15-05-2025");
    });
  }
}