package com.example.cinema.service;

import com.example.cinema.model.User;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service class for managing users.
 * Provides methods for fetching all users, filtering them by age, and retrieving a user by ID.
 */
@Service
public class UserService {

  /** A list of pre-defined users. */
  private final List<User> users = List.of(
          new User(1, "Павел", "Левикин", "pavel123@gmail.com", 18),
          new User(2, "Дмитрий", "Сидоров", "DDDSidorov@gmail.com", 24),
          new User(3, "Оля", "Лаврова", "olhaL@gmail.com", 20)
  );

  /**
   * Retrieves all users.
   *
   * @return a list of all users
   */
  public List<User> getAllUsers() {
    return users;
  }

  /**
   * Retrieves users filtered by age.
   *
   * @param age the age to filter users by, can be null
   * @return a list of users with the specified age, or all users if age is null
   */
  public List<User> getUsersByAge(Integer age) {
    if (age != null) {
      return users.stream()
              .filter(user -> user.getAge() == age)
              .toList();
    }
    return users;
  }

  /**
   * Retrieves a user by their ID.
   *
   * @param id the ID of the user
   * @return the user with the specified ID
   * @throws RuntimeException if the user is not found
   */
  public User getUserById(int id) {
    return users.stream()
            .filter(user -> user.getId() == id)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("User not found"));
  }
}
