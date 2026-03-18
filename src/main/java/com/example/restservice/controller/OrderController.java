package com.example.restservice.controller;

import com.example.restservice.exception.AccessDeniedException;
import com.example.restservice.model.Action;
import com.example.restservice.model.Country;
import com.example.restservice.model.MenuItem;
import com.example.restservice.model.Order;
import com.example.restservice.model.OrderItem;
import com.example.restservice.model.OrderStatus;
import com.example.restservice.model.Restaurant;
import com.example.restservice.model.Role;
import com.example.restservice.model.User;
import com.example.restservice.service.AccessControlService;
import com.example.restservice.service.CurrentUserResolver;
import com.example.restservice.service.DataStore;
import com.example.restservice.service.EmailService;
import com.example.restservice.service.PaymentGateway;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {

  private final DataStore dataStore;
  private final CurrentUserResolver currentUserResolver;
  private final AccessControlService accessControlService;
  private final PaymentGateway paymentGateway;
  private final EmailService emailService;

  public OrderController(
      DataStore dataStore,
      CurrentUserResolver currentUserResolver,
      AccessControlService accessControlService,
      PaymentGateway paymentGateway,
      EmailService emailService
  ) {
    this.dataStore = dataStore;
    this.currentUserResolver = currentUserResolver;
    this.accessControlService = accessControlService;
    this.paymentGateway = paymentGateway;
    this.emailService = emailService;
  }

  public record CreateOrderItemRequest(long menuItemId, int quantity) { }

  public record CreateOrderRequest(long restaurantId, List<CreateOrderItemRequest> items) { }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Order createOrder(@RequestBody CreateOrderRequest request) {
    User user = currentUserResolver.getCurrentUser();
    accessControlService.checkAccess(user, Action.CREATE_ORDER);

    Restaurant restaurant = dataStore.findRestaurantById(request.restaurantId())
        .orElseThrow(() -> new IllegalArgumentException("Restaurant not found: " + request.restaurantId()));

    if (user.getRole() != Role.ADMIN && user.getCountry() != restaurant.getCountry()) {
      throw new AccessDeniedException("Cannot create order for restaurant in another country");
    }

    List<OrderItem> orderItems = request.items().stream()
        .map(reqItem -> {
          MenuItem menuItem = restaurant.getMenuItems().stream()
              .filter(mi -> mi.getId() == reqItem.menuItemId())
              .findFirst()
              .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + reqItem.menuItemId()));
          return new OrderItem(
              menuItem.getId(),
              menuItem.getName(),
              menuItem.getPrice(),
              reqItem.quantity()
          );
        })
        .collect(Collectors.toList());

    Order order = new Order(
        0L,
        user.getId(),
        restaurant.getCountry(),
        restaurant.getId(),
        orderItems,
        OrderStatus.CREATED,
        null,
        null
    );

    return dataStore.saveNewOrder(order);
  }

  @GetMapping
  public List<Order> listOrders() {
    User user = currentUserResolver.getCurrentUser();

    if (user.getRole() == Role.ADMIN || user.getCountry() == null) {
      return dataStore.findAllOrders();
    }

    if (user.getRole() == Role.MANAGER) {
      return dataStore.findOrdersInCountry(user.getCountry());
    } else {
      return dataStore.findOrdersForUserInCountry(user.getId(), user.getCountry());
    }
  }

  public record PlaceOrderRequest(Long paymentMethodId, String paymentDetails) { }

  @PatchMapping("/{id}/place")
  public Order placeOrder(@PathVariable long id, @RequestBody PlaceOrderRequest request) {
    User user = currentUserResolver.getCurrentUser();
    accessControlService.checkAccess(user, Action.PLACE_ORDER);

    Order order = dataStore.findOrderById(id)
        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));

    if (user.getRole() != Role.ADMIN && user.getCountry() != order.getCountry()) {
      throw new AccessDeniedException("Cannot place order in another country");
    }

    var result = paymentGateway.processPayment(order, request.paymentDetails());
    if (!result.success()) {
      throw new IllegalStateException("Payment failed: " + result.message());
    }

    order.setStatus(OrderStatus.PLACED);
    order.setPaymentMethodId(request.paymentMethodId());
    order.setPaymentReference(result.referenceId());
    dataStore.updateOrder(order);

    User userForEmail = currentUserResolver.getCurrentUser();
    emailService.sendOrderReceipt(userForEmail, order);
    return order;
  }

  @PatchMapping("/{id}/cancel")
  public Order cancelOrder(@PathVariable long id) {
    User user = currentUserResolver.getCurrentUser();
    accessControlService.checkAccess(user, Action.CANCEL_ORDER);

    Order order = dataStore.findOrderById(id)
        .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));

    if (user.getRole() != Role.ADMIN && user.getCountry() != order.getCountry()) {
      throw new AccessDeniedException("Cannot cancel order in another country");
    }

    order.setStatus(OrderStatus.CANCELLED);
    dataStore.updateOrder(order);
    return order;
  }
}

