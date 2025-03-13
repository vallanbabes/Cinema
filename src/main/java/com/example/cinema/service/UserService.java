package com.example.cinema.service;

import com.example.cinema.model.User;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
   * @throws ResponseStatusException if no users with the specified age are found
   */
  public List<User> getUsersByAge(Integer age) {
    if (age != null) {
      List<User> filteredUsers = users.stream()
              .filter(user -> user.getAge() == age)
              .toList();

      if (filteredUsers.isEmpty()) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Users with age " + age + " not found"
        );
      }
      return filteredUsers;
    }
    return users;
  }

  /**
   * Retrieves a user by their ID.
   *
   * @param id the ID of the user
   * @return the user with the specified ID
   * @throws ResponseStatusException if the user is not found
   */
  public User getUserById(int id) {
    return users.stream()
            .filter(user -> user.getId() == id)
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "User with ID " + id + " not found"
            ));
  }
}
