package com.example.restservice.service;

import com.example.restservice.exception.AccessDeniedException;
import com.example.restservice.model.Action;
import com.example.restservice.model.Role;
import com.example.restservice.model.User;
import org.springframework.stereotype.Service;

@Service
public class AccessControlService {

  public void checkAccess(User user, Action action) {
    if (!hasAccess(user, action)) {
      throw new AccessDeniedException(
          "User %s with role %s is not allowed to perform %s"
              .formatted(user.getName(), user.getRole(), action)
      );
    }
  }

  public boolean hasAccess(User user, Action action) {
    return switch (action) {
      case VIEW_RESTAURANTS, CREATE_ORDER ->
          true;
      case PLACE_ORDER, CANCEL_ORDER ->
          user.getRole() == Role.ADMIN || user.getRole() == Role.MANAGER;
      case UPDATE_PAYMENT_METHOD ->
          user.getRole() == Role.ADMIN;
    };
  }
}

