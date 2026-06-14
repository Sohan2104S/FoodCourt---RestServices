package com.example.restservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class PaymentMethod {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private long userId;
  private String methodType;
  private String details;

  public PaymentMethod() {}

  public PaymentMethod(long id, long userId, String methodType, String details) {
    this.id = id;
    this.userId = userId;
    this.methodType = methodType;
    this.details = details;
  }

  public PaymentMethod(long userId, String methodType, String details) {
    this.userId = userId;
    this.methodType = methodType;
    this.details = details;
  }

  public long getId() { return id; }
  public void setId(long id) { this.id = id; }
  public long getUserId() { return userId; }
  public void setUserId(long userId) { this.userId = userId; }
  public String getMethodType() { return methodType; }
  public void setMethodType(String methodType) { this.methodType = methodType; }
  public String getDetails() { return details; }
  public void setDetails(String details) { this.details = details; }
}
