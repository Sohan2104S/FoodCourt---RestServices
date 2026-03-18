package com.example.restservice.model;

import java.util.List;

public class Restaurant {

  private final long id;
  private final String name;
  private final Country country;
  private final List<MenuItem> menuItems;

  public Restaurant(long id, String name, Country country, List<MenuItem> menuItems) {
    this.id = id;
    this.name = name;
    this.country = country;
    this.menuItems = menuItems;
  }

  public long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Country getCountry() {
    return country;
  }

  public List<MenuItem> getMenuItems() {
    return menuItems;
  }
}

