package com.example.restservice.service;

import com.example.restservice.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class CurrentUserResolver {

  private final User currentUser;

  public CurrentUserResolver(DataStore dataStore, HttpServletRequest request) {
    String userIdHeader = request.getHeader("X-USER-ID");
    long resolvedUserId;
    try {
      resolvedUserId = userIdHeader != null ? Long.parseLong(userIdHeader) : 1L;
    } catch (NumberFormatException e) {
      resolvedUserId = 1L;
    }

    final long userId = resolvedUserId;
    this.currentUser = dataStore.findUserById(userId)
        .orElseThrow(() -> new IllegalArgumentException("Unknown user id: " + userId));
  }

  public User getCurrentUser() {
    return currentUser;
  }
}

