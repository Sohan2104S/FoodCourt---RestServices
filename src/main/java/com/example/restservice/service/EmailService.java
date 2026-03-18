package com.example.restservice.service;

import com.example.restservice.model.Order;
import com.example.restservice.model.OrderItem;
import com.example.restservice.model.User;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private static final Logger log = LoggerFactory.getLogger(EmailService.class);

  public void sendOrderReceipt(User user, Order order) {
    StringBuilder body = new StringBuilder();
    body.append("Hi ").append(user.getName()).append(",\n\n");
    body.append("Thank you for your order ").append(order.getId()).append(".\n");
    body.append("Status: ").append(order.getStatus()).append("\n");
    body.append("Payment reference: ").append(order.getPaymentReference()).append("\n\n");
    body.append("Items:\n");

    for (OrderItem item : order.getItems()) {
      BigDecimal lineTotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
      body.append("- ")
          .append(item.getMenuItemName())
          .append(" x ")
          .append(item.getQuantity())
          .append(" = ")
          .append(lineTotal)
          .append("\n");
    }

    body.append("\nTotal: ").append(order.getTotalAmount()).append("\n\n");
    body.append("This is a demo email log; configure SMTP to actually send emails.\n");

    log.info("Sending order receipt email to {} ({}):\n{}", user.getEmail(), user.getName(), body);
  }
}

