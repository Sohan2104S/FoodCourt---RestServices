package com.example.restservice.model;

public class User {

  private final long id;
  private final String name;
  private final Role role;
  private final Country country;
  private final String email;

  public User(long id, String name, Role role, Country country, String email) {
    this.id = id;
    this.name = name;
    this.role = role;
    this.country = country;
    this.email = email;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Role getRole() {
    return role;
  }

  public Country getCountry() {
    return country;
  }

  public String getEmail() {
    return email;
  }
}

