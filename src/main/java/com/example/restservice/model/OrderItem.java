package com.example.restservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;

@Entity
public class OrderItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private long menuItemId;
  private String menuItemName;
  private BigDecimal price;
  private int quantity;

  public OrderItem() {}

  public OrderItem(long menuItemId, String menuItemName, BigDecimal price, int quantity) {
    this.menuItemId = menuItemId;
    this.menuItemName = menuItemName;
    this.price = price;
    this.quantity = quantity;
  }

  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  public long getMenuItemId() { return menuItemId; }
  public void setMenuItemId(long menuItemId) { this.menuItemId = menuItemId; }
  public String getMenuItemName() { return menuItemName; }
  public void setMenuItemName(String menuItemName) { this.menuItemName = menuItemName; }
  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal price) { this.price = price; }
  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) { this.quantity = quantity; }
}
