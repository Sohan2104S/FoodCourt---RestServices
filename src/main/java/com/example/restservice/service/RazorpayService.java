package com.example.restservice.service;

import com.example.restservice.config.RazorpayProperties;
import com.example.restservice.model.Order;
import com.example.restservice.model.User;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

  private static final Logger log = LoggerFactory.getLogger(RazorpayService.class);

  private final RazorpayProperties properties;

  public RazorpayService(RazorpayProperties properties) {
    this.properties = properties;
  }

  public boolean isConfigured() {
    return properties.isEnabled();
  }

  public String getKeyId() {
    return properties.getKeyId();
  }

  public RazorpayCheckout createCheckoutOrder(Order order, User user) throws RazorpayException {
    RazorpayClient client = new RazorpayClient(properties.getKeyId(), properties.getKeySecret());

    int amountInSmallestUnit = toSmallestCurrencyUnit(order.getTotalAmount());

    JSONObject options = new JSONObject();
    options.put("amount", amountInSmallestUnit);
    options.put("currency", properties.getCurrency());
    options.put("receipt", "food_order_" + order.getId());
    options.put("notes", new JSONObject()
        .put("appOrderId", order.getId())
        .put("userId", user.getId())
        .put("userEmail", user.getEmail()));

    com.razorpay.Order razorpayOrder = client.orders.create(options);

    return new RazorpayCheckout(
        properties.getKeyId(),
        razorpayOrder.get("id"),
        amountInSmallestUnit,
        properties.getCurrency(),
        user.getName(),
        user.getEmail()
    );
  }

  public boolean verifyPaymentSignature(String razorpayOrderId, String paymentId, String signature) {
    try {
      JSONObject attributes = new JSONObject();
      attributes.put("razorpay_order_id", razorpayOrderId);
      attributes.put("razorpay_payment_id", paymentId);
      attributes.put("razorpay_signature", signature);
      return Utils.verifyPaymentSignature(attributes, properties.getKeySecret());
    } catch (Exception e) {
      log.warn("Razorpay signature verification failed: {}", e.getMessage());
      return false;
    }
  }

  private int toSmallestCurrencyUnit(BigDecimal amount) {
    return amount.multiply(BigDecimal.valueOf(100))
        .setScale(0, RoundingMode.HALF_UP)
        .intValueExact();
  }

  public record RazorpayCheckout(
      String keyId,
      String razorpayOrderId,
      int amount,
      String currency,
      String customerName,
      String customerEmail
  ) { }
}
