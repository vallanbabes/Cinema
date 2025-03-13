package com.example.cinema.controller;

import com.example.cinema.model.User;
import com.example.cinema.service.UserService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for handling user-related requests.
 * Provides endpoints for fetching users and filtering them by criteria.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  /** The user service to handle user-related business logic. */
  private final UserService userService;

  /**
   * Constructor for initializing the UserController with the UserService.
   *
   * @param userService the service to manage users
   */
  public UserController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Fetches all users.
   *
   * @return a list of all users
   */
  @GetMapping
  public List<User> findAllUsers() {
    return userService.getAllUsers();
  }

  /**
   * Fetches users filtered by age.
   *
   * @param age the age to filter users by, can be null
   * @return a list of users filtered by age
   */
  @GetMapping("/filter")
  public List<User> findUsersByAge(@RequestParam(required = false) Integer age) {
    return userService.getUsersByAge(age);
  }

  /**
   * Fetches a user by their ID.
   *
   * @param id the ID of the user
   * @return the user with the specified ID
   */
  @GetMapping("/{id}")
  public User findUserById(@PathVariable int id) {
    return userService.getUserById(id);
  }
}
