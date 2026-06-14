package com.example.restservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class Restaurant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String name;
  
  @Enumerated(EnumType.STRING)
  private Country country;
  
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  private List<MenuItem> menuItems;

  public Restaurant() {}

  public Restaurant(long id, String name, Country country, List<MenuItem> menuItems) {
    this.id = id;
    this.name = name;
    this.country = country;
    this.menuItems = menuItems;
  }

  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public Country getCountry() { return country; }
  public void setCountry(Country country) { this.country = country; }
  public List<MenuItem> getMenuItems() { return menuItems; }
  public void setMenuItems(List<MenuItem> menuItems) { this.menuItems = menuItems; }
}
