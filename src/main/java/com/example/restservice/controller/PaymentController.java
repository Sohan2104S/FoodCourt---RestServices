package com.example.restservice.controller;

import com.example.restservice.config.RazorpayProperties;
import com.example.restservice.model.Action;
import com.example.restservice.model.PaymentMethod;
import com.example.restservice.model.User;
import com.example.restservice.service.AccessControlService;
import com.example.restservice.service.CurrentUserResolver;
import com.example.restservice.service.DataStore;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

  private final DataStore dataStore;
  private final CurrentUserResolver currentUserResolver;
  private final AccessControlService accessControlService;
  private final RazorpayProperties razorpayProperties;

  public PaymentController(
      DataStore dataStore,
      CurrentUserResolver currentUserResolver,
      AccessControlService accessControlService,
      RazorpayProperties razorpayProperties
  ) {
    this.dataStore = dataStore;
    this.currentUserResolver = currentUserResolver;
    this.accessControlService = accessControlService;
    this.razorpayProperties = razorpayProperties;
  }

  public record PaymentConfigResponse(boolean razorpayEnabled, String razorpayKeyId) { }

  @GetMapping("/config")
  public PaymentConfigResponse getPaymentConfig() {
    return new PaymentConfigResponse(
        razorpayProperties.isEnabled(),
        razorpayProperties.isEnabled() ? razorpayProperties.getKeyId() : null
    );
  }

  @GetMapping
  public List<PaymentMethod> listMyPaymentMethods() {
    User user = currentUserResolver.getCurrentUser();
    return dataStore.findPaymentMethodsForUser(user.getId());
  }

  public record UpdatePaymentMethodRequest(String methodType, String details) { }

  @PatchMapping
  public PaymentMethod updateMyPaymentMethod(@RequestBody UpdatePaymentMethodRequest request) {
    User user = currentUserResolver.getCurrentUser();
    accessControlService.checkAccess(user, Action.UPDATE_PAYMENT_METHOD);

    return dataStore.saveOrUpdatePaymentMethod(
        user.getId(),
        request.methodType(),
        request.details()
    );
  }
}

