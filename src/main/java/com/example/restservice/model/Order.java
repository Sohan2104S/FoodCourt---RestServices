package com.example.restservice.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private long userId;
  
  @Enumerated(EnumType.STRING)
  private Country country;
  
  private long restaurantId;
  
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items;
  
  @Enumerated(EnumType.STRING)
  private OrderStatus status;
  
  private Long paymentMethodId;
  private String paymentReference;

  public Order() {}

  public Order(long id, long userId, Country country, long restaurantId, List<OrderItem> items, OrderStatus status, Long paymentMethodId, String paymentReference) {
    this.id = id;
    this.userId = userId;
    this.country = country;
    this.restaurantId = restaurantId;
    this.items = items;
    this.status = status;
    this.paymentMethodId = paymentMethodId;
    this.paymentReference = paymentReference;
  }
  
  public Order(long userId, Country country, long restaurantId, List<OrderItem> items, OrderStatus status) {
    this.userId = userId;
    this.country = country;
    this.restaurantId = restaurantId;
    this.items = items;
    this.status = status;
  }

  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  public long getUserId() { return userId; }
  public void setUserId(long userId) { this.userId = userId; }
  public Country getCountry() { return country; }
  public void setCountry(Country country) { this.country = country; }
  public long getRestaurantId() { return restaurantId; }
  public void setRestaurantId(long restaurantId) { this.restaurantId = restaurantId; }
  public List<OrderItem> getItems() { return items; }
  public void setItems(List<OrderItem> items) { this.items = items; }
  public OrderStatus getStatus() { return status; }
  public void setStatus(OrderStatus status) { this.status = status; }
  public Long getPaymentMethodId() { return paymentMethodId; }
  public void setPaymentMethodId(Long paymentMethodId) { this.paymentMethodId = paymentMethodId; }
  public String getPaymentReference() { return paymentReference; }
  public void setPaymentReference(String paymentReference) { this.paymentReference = paymentReference; }

  public BigDecimal getTotalAmount() {
    if (items == null) return BigDecimal.ZERO;
    return items.stream()
        .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
