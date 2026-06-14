package com.example.restservice.service;

import com.example.restservice.model.Country;
import com.example.restservice.model.Order;
import com.example.restservice.model.PaymentMethod;
import com.example.restservice.model.Restaurant;
import com.example.restservice.model.User;
import com.example.restservice.repository.OrderRepository;
import com.example.restservice.repository.PaymentMethodRepository;
import com.example.restservice.repository.RestaurantRepository;
import com.example.restservice.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DataStore {

  private final UserRepository userRepository;
  private final RestaurantRepository restaurantRepository;
  private final OrderRepository orderRepository;
  private final PaymentMethodRepository paymentMethodRepository;

  public DataStore(UserRepository userRepository, RestaurantRepository restaurantRepository,
                   OrderRepository orderRepository, PaymentMethodRepository paymentMethodRepository) {
    this.userRepository = userRepository;
    this.restaurantRepository = restaurantRepository;
    this.orderRepository = orderRepository;
    this.paymentMethodRepository = paymentMethodRepository;
  }

  public Optional<User> findUserById(long id) {
    return userRepository.findById(id);
  }

  public List<User> findAllUsers() {
    return userRepository.findAll();
  }

  public List<Restaurant> findAllRestaurants() {
    return restaurantRepository.findAll();
  }

  public Optional<Restaurant> findRestaurantById(long id) {
    return restaurantRepository.findById(id);
  }

  public List<Order> findOrdersForUserInCountry(long userId, Country country) {
    return orderRepository.findByUserIdAndCountry(userId, country);
  }

  public List<Order> findOrdersInCountry(Country country) {
    return orderRepository.findByCountry(country);
  }

  public List<Order> findAllOrders() {
    return orderRepository.findAll();
  }

  public Optional<Order> findOrderById(long id) {
    return orderRepository.findById(id);
  }

  public Order saveNewOrder(Order order) {
    return orderRepository.save(order);
  }

  public void updateOrder(Order order) {
    orderRepository.save(order);
  }

  public List<PaymentMethod> findPaymentMethodsForUser(long userId) {
    return paymentMethodRepository.findByUserId(userId);
  }

  public PaymentMethod saveOrUpdatePaymentMethod(long userId, String methodType, String details) {
    List<PaymentMethod> existing = findPaymentMethodsForUser(userId);
    if (existing.isEmpty()) {
      PaymentMethod pm = new PaymentMethod(userId, methodType, details);
      return paymentMethodRepository.save(pm);
    } else {
      PaymentMethod pm = existing.get(0);
      pm.setMethodType(methodType);
      pm.setDetails(details);
      return paymentMethodRepository.save(pm);
    }
  }

  public Map<Long, Restaurant> getRestaurantsReadOnly() {
    return restaurantRepository.findAll().stream()
        .collect(Collectors.toMap(Restaurant::getId, Function.identity()));
  }
}
