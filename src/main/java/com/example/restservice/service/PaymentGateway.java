package com.example.restservice.service;

import com.example.restservice.model.Order;

public interface PaymentGateway {

  PaymentResult processPayment(Order order, String paymentDetails);

  record PaymentResult(boolean success, String referenceId, String message) { }
}

