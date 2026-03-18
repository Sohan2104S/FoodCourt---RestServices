package com.example.restservice.controller;

import com.example.restservice.model.Action;
import com.example.restservice.model.Restaurant;
import com.example.restservice.model.Role;
import com.example.restservice.model.User;
import com.example.restservice.service.AccessControlService;
import com.example.restservice.service.CurrentUserResolver;
import com.example.restservice.service.DataStore;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

  private final DataStore dataStore;
  private final CurrentUserResolver currentUserResolver;
  private final AccessControlService accessControlService;

  public RestaurantController(
      DataStore dataStore,
      CurrentUserResolver currentUserResolver,
      AccessControlService accessControlService
  ) {
    this.dataStore = dataStore;
    this.currentUserResolver = currentUserResolver;
    this.accessControlService = accessControlService;
  }

  @GetMapping
  public List<Restaurant> listRestaurants() {
    User user = currentUserResolver.getCurrentUser();
    accessControlService.checkAccess(user, Action.VIEW_RESTAURANTS);

    if (user.getRole() == Role.ADMIN || user.getCountry() == null) {
      return dataStore.findAllRestaurants();
    }

    return dataStore.findAllRestaurants().stream()
        .filter(r -> r.getCountry() == user.getCountry())
        .collect(Collectors.toList());
  }
}

