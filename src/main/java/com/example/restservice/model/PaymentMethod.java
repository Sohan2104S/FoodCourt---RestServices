package com.example.restservice.model;

public class PaymentMethod {

  private final long id;
  private final long userId;
  private String methodType;
  private String details;

  public PaymentMethod(long id, long userId, String methodType, String details) {
    this.id = id;
    this.userId = userId;
    this.methodType = methodType;
    this.details = details;
  }

  public long getId() {
    return id;
  }

  public long getUserId() {
    return userId;
  }

  public String getMethodType() {
    return methodType;
  }

  public void setMethodType(String methodType) {
    this.methodType = methodType;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
  }
}

