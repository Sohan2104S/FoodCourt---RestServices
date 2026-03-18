package com.example.restservice.model;

import java.math.BigDecimal;
import java.util.List;

public class Order {

  private final long id;
  private final long userId;
  private final Country country;
  private final long restaurantId;
  private final List<OrderItem> items;
  private OrderStatus status;
  private Long paymentMethodId;
  private String paymentReference;

  public Order(
      long id,
      long userId,
      Country country,
      long restaurantId,
      List<OrderItem> items,
      OrderStatus status,
      Long paymentMethodId,
      String paymentReference
  ) {
    this.id = id;
    this.userId = userId;
    this.country = country;
    this.restaurantId = restaurantId;
    this.items = items;
    this.status = status;
    this.paymentMethodId = paymentMethodId;
    this.paymentReference = paymentReference;
  }

  public long getId() {
    return id;
  }

  public long getUserId() {
    return userId;
  }

  public Country getCountry() {
    return country;
  }

  public long getRestaurantId() {
    return restaurantId;
  }

  public List<OrderItem> getItems() {
    return items;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void setStatus(OrderStatus status) {
    this.status = status;
  }

  public Long getPaymentMethodId() {
    return paymentMethodId;
  }

  public void setPaymentMethodId(Long paymentMethodId) {
    this.paymentMethodId = paymentMethodId;
  }

  public String getPaymentReference() {
    return paymentReference;
  }

  public void setPaymentReference(String paymentReference) {
    this.paymentReference = paymentReference;
  }

  public BigDecimal getTotalAmount() {
    return items.stream()
        .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}

