package com.example.restservice.service;

import com.example.restservice.model.Country;
import com.example.restservice.model.MenuItem;
import com.example.restservice.model.Order;
import com.example.restservice.model.PaymentMethod;
import com.example.restservice.model.Restaurant;
import com.example.restservice.model.Role;
import com.example.restservice.model.User;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

@Component
public class DataStore {

  private final Map<Long, User> users = new HashMap<>();
  private final Map<Long, Restaurant> restaurants = new HashMap<>();
  private final Map<Long, PaymentMethod> paymentMethods = new HashMap<>();
  private final Map<Long, Order> orders = new HashMap<>();

  private final AtomicLong orderIdSeq = new AtomicLong(1);
  private final AtomicLong paymentIdSeq = new AtomicLong(1);

  public DataStore() {
    seedUsers();
    seedRestaurants();
    seedPaymentMethods();
  }

  private void seedUsers() {
    users.put(1L, new User(1L, "Nick Fury", Role.ADMIN, null, "nick.fury@example.com"));
    users.put(2L, new User(2L, "Captain Marvel", Role.MANAGER, Country.INDIA, "captain.marvel@example.com"));
    users.put(3L, new User(3L, "Captain America", Role.MANAGER, Country.AMERICA, "captain.america@example.com"));
    users.put(4L, new User(4L, "Thanos", Role.MEMBER, Country.INDIA, "thanos@example.com"));
    users.put(5L, new User(5L, "Thor", Role.MEMBER, Country.INDIA, "thor@example.com"));
    users.put(6L, new User(6L, "Travis", Role.MEMBER, Country.AMERICA, "travis@example.com"));
  }

  private void seedRestaurants() {
    Restaurant mumbaiSpice = new Restaurant(
        1L,
        "Mumbai Spice",
        Country.INDIA,
        List.of(
            new MenuItem(1L, "Butter Chicken", BigDecimal.valueOf(300)),
            new MenuItem(2L, "Paneer Tikka", BigDecimal.valueOf(250)),
            new MenuItem(9L, "Garlic Naan", BigDecimal.valueOf(60)),
            new MenuItem(10L, "Gulab Jamun", BigDecimal.valueOf(90))
        )
    );

    Restaurant bangaloreBytes = new Restaurant(
        2L,
        "Bangalore Bytes",
        Country.INDIA,
        List.of(
            new MenuItem(3L, "Masala Dosa", BigDecimal.valueOf(120)),
            new MenuItem(4L, "Idli Sambar", BigDecimal.valueOf(80)),
            new MenuItem(11L, "Filter Coffee", BigDecimal.valueOf(70)),
            new MenuItem(12L, "Vada", BigDecimal.valueOf(50))
        )
    );

    Restaurant nycPizza = new Restaurant(
        3L,
        "NYC Pizza Co.",
        Country.AMERICA,
        List.of(
            new MenuItem(5L, "Pepperoni Pizza", BigDecimal.valueOf(15)),
            new MenuItem(6L, "Veggie Pizza", BigDecimal.valueOf(14)),
            new MenuItem(13L, "Garlic Bread", BigDecimal.valueOf(6)),
            new MenuItem(14L, "Caesar Salad", BigDecimal.valueOf(9))
        )
    );

    Restaurant laBurgers = new Restaurant(
        4L,
        "LA Burgers",
        Country.AMERICA,
        List.of(
            new MenuItem(7L, "Cheeseburger", BigDecimal.valueOf(10)),
            new MenuItem(8L, "Fries", BigDecimal.valueOf(4)),
            new MenuItem(15L, "Milkshake", BigDecimal.valueOf(5)),
            new MenuItem(16L, "Onion Rings", BigDecimal.valueOf(4.5))
        )
    );

    restaurants.put(mumbaiSpice.getId(), mumbaiSpice);
    restaurants.put(bangaloreBytes.getId(), bangaloreBytes);
    restaurants.put(nycPizza.getId(), nycPizza);
    restaurants.put(laBurgers.getId(), laBurgers);
  }

  private void seedPaymentMethods() {
    long pmId = paymentIdSeq.getAndIncrement();
    paymentMethods.put(pmId, new PaymentMethod(pmId, 1L, "CARD", "**** **** **** 4242"));
  }

  public Optional<User> findUserById(long id) {
    return Optional.ofNullable(users.get(id));
  }

  public List<User> findAllUsers() {
    return new ArrayList<>(users.values());
  }

  public List<Restaurant> findAllRestaurants() {
    return new ArrayList<>(restaurants.values());
  }

  public Optional<Restaurant> findRestaurantById(long id) {
    return Optional.ofNullable(restaurants.get(id));
  }

  public List<Order> findOrdersForUserInCountry(long userId, Country country) {
    List<Order> result = new ArrayList<>();
    for (Order order : orders.values()) {
      if (order.getUserId() == userId && order.getCountry() == country) {
        result.add(order);
      }
    }
    return result;
  }

  public List<Order> findOrdersInCountry(Country country) {
    List<Order> result = new ArrayList<>();
    for (Order order : orders.values()) {
      if (order.getCountry() == country) {
        result.add(order);
      }
    }
    return result;
  }

  public List<Order> findAllOrders() {
    return new ArrayList<>(orders.values());
  }

  public Optional<Order> findOrderById(long id) {
    return Optional.ofNullable(orders.get(id));
  }

  public Order saveNewOrder(Order order) {
    long id = orderIdSeq.getAndIncrement();
    Order withId = new Order(
        id,
        order.getUserId(),
        order.getCountry(),
        order.getRestaurantId(),
        order.getItems(),
        order.getStatus(),
        order.getPaymentMethodId(),
        order.getPaymentReference()
    );
    orders.put(id, withId);
    return withId;
  }

  public void updateOrder(Order order) {
    orders.put(order.getId(), order);
  }

  public List<PaymentMethod> findPaymentMethodsForUser(long userId) {
    List<PaymentMethod> result = new ArrayList<>();
    for (PaymentMethod pm : paymentMethods.values()) {
      if (pm.getUserId() == userId) {
        result.add(pm);
      }
    }
    return result;
  }

  public PaymentMethod saveOrUpdatePaymentMethod(long userId, String methodType, String details) {
    List<PaymentMethod> existing = findPaymentMethodsForUser(userId);
    if (existing.isEmpty()) {
      long id = paymentIdSeq.getAndIncrement();
      PaymentMethod pm = new PaymentMethod(id, userId, methodType, details);
      paymentMethods.put(id, pm);
      return pm;
    } else {
      PaymentMethod pm = existing.get(0);
      pm.setMethodType(methodType);
      pm.setDetails(details);
      return pm;
    }
  }

  public Map<Long, Restaurant> getRestaurantsReadOnly() {
    return Collections.unmodifiableMap(restaurants);
  }
}

