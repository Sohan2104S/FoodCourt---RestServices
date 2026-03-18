package com.example.restservice.model;

import java.math.BigDecimal;

public class OrderItem {

  private final long menuItemId;
  private final String menuItemName;
  private final BigDecimal price;
  private final int quantity;

  public OrderItem(long menuItemId, String menuItemName, BigDecimal price, int quantity) {
    this.menuItemId = menuItemId;
    this.menuItemName = menuItemName;
    this.price = price;
    this.quantity = quantity;
  }

  public long getMenuItemId() {
    return menuItemId;
  }

  public String getMenuItemName() {
    return menuItemName;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public int getQuantity() {
    return quantity;
  }
}

