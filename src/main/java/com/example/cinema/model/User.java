package com.example.cinema.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a user in the cinema system.
 * This class is a data model for user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
  private int id;
  private String firstName;
  private String lastName;
  private String email;
  private int age;
}
