package com.example.restservice.controller;

import com.example.restservice.exception.AccessDeniedException;
import com.example.restservice.model.Action;
import com.example.restservice.model.Order;
import com.example.restservice.model.OrderStatus;
import com.example.restservice.model.Role;
import com.example.restservice.model.User;
import com.example.restservice.service.AccessControlService;
import com.example.restservice.service.CurrentUserResolver;
import com.example.restservice.service.DataStore;
import com.example.restservice.service.EmailService;
import com.example.restservice.service.RazorpayService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments/razorpay")
public class RazorpayController {

  private final DataStore dataStore;
  private final CurrentUserResolver currentUserResolver;
  private final AccessControlService accessControlService;
  private final RazorpayService razorpayService;
  private final EmailService emailService;

  public RazorpayController(
      DataStore dataStore,
      CurrentUserResolver currentUserResolver,
      AccessControlService accessControlService,
      RazorpayService razorpayService,
      EmailService emailService
  ) {
    this.dataStore = dataStore;
    this.currentUserResolver = currentUserResolver;
    this.accessControlService = accessControlService;
    this.razorpayService = razorpayService;
    this.emailService = emailService;
  }

  @PostMapping("/orders/{orderId}/create")
  @ResponseStatus(HttpStatus.CREATED)
  public RazorpayService.RazorpayCheckout createRazorpayOrder(@PathVariable long orderId) throws Exception {
    if (!razorpayService.isConfigured()) {
      throw new IllegalStateException("Razorpay is not configured. Add test keys in application-local.properties");
    }

    User user = currentUserResolver.getCurrentUser();
    accessControlService.checkAccess(user, Action.PLACE_ORDER);

    Order order = dataStore.findOrderById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

    assertCountryAccess(user, order);

    if (order.getStatus() != OrderStatus.CREATED) {
      throw new IllegalStateException("Order is not payable. Status: " + order.getStatus());
    }

    return razorpayService.createCheckoutOrder(order, user);
  }

  public record VerifyPaymentRequest(
      String razorpayOrderId,
      String razorpayPaymentId,
      String razorpaySignature
  ) { }

  @PostMapping("/orders/{orderId}/verify")
  public Order verifyAndPlaceOrder(@PathVariable long orderId, @RequestBody VerifyPaymentRequest request) {
    if (!razorpayService.isConfigured()) {
      throw new IllegalStateException("Razorpay is not configured");
    }

    User user = currentUserResolver.getCurrentUser();
    accessControlService.checkAccess(user, Action.PLACE_ORDER);

    Order order = dataStore.findOrderById(orderId)
        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

    assertCountryAccess(user, order);

    boolean valid = razorpayService.verifyPaymentSignature(
        request.razorpayOrderId(),
        request.razorpayPaymentId(),
        request.razorpaySignature()
    );

    if (!valid) {
      throw new IllegalStateException("Payment verification failed. Invalid signature.");
    }

    order.setStatus(OrderStatus.PLACED);
    order.setPaymentReference("RZP-" + request.razorpayPaymentId());
    dataStore.updateOrder(order);

    emailService.sendOrderReceipt(user, order);
    return order;
  }

  private void assertCountryAccess(User user, Order order) {
    if (user.getRole() != Role.ADMIN && user.getCountry() != order.getCountry()) {
      throw new AccessDeniedException("Cannot pay for order in another country");
    }
  }
}
