package com.example.restservice.repository;

import com.example.restservice.model.Order;
import com.example.restservice.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdAndCountry(long userId, Country country);
    List<Order> findByCountry(Country country);
}
