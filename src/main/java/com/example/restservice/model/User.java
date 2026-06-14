package com.example.restservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  
  @Enumerated(EnumType.STRING)
  private Role role;
  
  @Enumerated(EnumType.STRING)
  private Country country;
  
  private String email;

  public User() {}

  public User(long id, String name, Role role, Country country, String email) {
    this.id = id;
    this.name = name;
    this.role = role;
    this.country = country;
    this.email = email;
  }

  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public Role getRole() { return role; }
  public void setRole(Role role) { this.role = role; }
  public Country getCountry() { return country; }
  public void setCountry(Country country) { this.country = country; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
}
