package com.example.restservice.service;

import com.example.restservice.model.Order;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DemoPaymentGatewayService implements PaymentGateway {

  private static final Logger log = LoggerFactory.getLogger(DemoPaymentGatewayService.class);

  @Override
  public PaymentResult processPayment(Order order, String paymentDetails) {
    // In a real app this is where we would call Stripe/Razorpay/etc.
    String reference = "PAY-" + UUID.randomUUID();
    log.info("Demo payment processed for order {} with ref {} and details {}", order.getId(), reference, paymentDetails);
    return new PaymentResult(true, reference, "Payment approved (demo)");
  }
}

