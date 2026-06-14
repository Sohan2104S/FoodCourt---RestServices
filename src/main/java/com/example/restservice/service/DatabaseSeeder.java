package com.example.restservice.service;

import com.example.restservice.model.Country;
import com.example.restservice.model.MenuItem;
import com.example.restservice.model.PaymentMethod;
import com.example.restservice.model.Restaurant;
import com.example.restservice.model.Role;
import com.example.restservice.model.User;
import com.example.restservice.repository.PaymentMethodRepository;
import com.example.restservice.repository.RestaurantRepository;
import com.example.restservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public DatabaseSeeder(UserRepository userRepository, RestaurantRepository restaurantRepository, PaymentMethodRepository paymentMethodRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            seedUsers();
            seedRestaurants();
            seedPaymentMethods();
        }
    }

    private void seedUsers() {
        userRepository.saveAll(List.of(
                new User(0L, "Nick Fury", Role.ADMIN, null, "nick.fury@example.com"),
                new User(0L, "Captain Marvel", Role.MANAGER, Country.INDIA, "captain.marvel@example.com"),
                new User(0L, "Captain America", Role.MANAGER, Country.AMERICA, "captain.america@example.com"),
                new User(0L, "Thanos", Role.MEMBER, Country.INDIA, "thanos@example.com"),
                new User(0L, "Thor", Role.MEMBER, Country.INDIA, "thor@example.com"),
                new User(0L, "Travis", Role.MEMBER, Country.AMERICA, "travis@example.com")
        ));
    }

    private void seedRestaurants() {
        Restaurant mumbaiSpice = new Restaurant(
                0L,
                "Mumbai Spice",
                Country.INDIA,
                List.of(
                        new MenuItem(0L, "Butter Chicken", BigDecimal.valueOf(300), "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398?w=500&q=80"),
                        new MenuItem(0L, "Paneer Tikka", BigDecimal.valueOf(250), "https://images.unsplash.com/photo-1599487405902-3bf96ceab514?w=500&q=80"),
                        new MenuItem(0L, "Garlic Naan", BigDecimal.valueOf(60), "https://images.unsplash.com/photo-1626200419199-391ae4be7a41?w=500&q=80"),
                        new MenuItem(0L, "Gulab Jamun", BigDecimal.valueOf(90), "https://images.unsplash.com/photo-1634818458760-44ec00d17676?w=500&q=80")
                )
        );

        Restaurant bangaloreBytes = new Restaurant(
                0L,
                "Bangalore Bytes",
                Country.INDIA,
                List.of(
                        new MenuItem(0L, "Masala Dosa", BigDecimal.valueOf(120), "https://images.unsplash.com/photo-1589301760014-d929f39ce9b1?w=500&q=80"),
                        new MenuItem(0L, "Idli Sambar", BigDecimal.valueOf(80), "https://images.unsplash.com/photo-1589301760014-d929f39ce9b1?w=500&q=80"),
                        new MenuItem(0L, "Filter Coffee", BigDecimal.valueOf(70), "https://images.unsplash.com/photo-1559525839-b184a4d698c7?w=500&q=80"),
                        new MenuItem(0L, "Vada", BigDecimal.valueOf(50), "https://images.unsplash.com/photo-1606491956689-2ea866880c84?w=500&q=80")
                )
        );

        Restaurant nycPizza = new Restaurant(
                0L,
                "NYC Pizza Co.",
                Country.AMERICA,
                List.of(
                        new MenuItem(0L, "Pepperoni Pizza", BigDecimal.valueOf(15), "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=500&q=80"),
                        new MenuItem(0L, "Veggie Pizza", BigDecimal.valueOf(14), "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=500&q=80"),
                        new MenuItem(0L, "Garlic Bread", BigDecimal.valueOf(6), "https://images.unsplash.com/photo-1573140247632-f8fd74997d5c?w=500&q=80"),
                        new MenuItem(0L, "Caesar Salad", BigDecimal.valueOf(9), "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=500&q=80")
                )
        );

        Restaurant laBurgers = new Restaurant(
                0L,
                "LA Burgers",
                Country.AMERICA,
                List.of(
                        new MenuItem(0L, "Cheeseburger", BigDecimal.valueOf(10), "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500&q=80"),
                        new MenuItem(0L, "Fries", BigDecimal.valueOf(4), "https://images.unsplash.com/photo-1576107232684-1279f3908594?w=500&q=80"),
                        new MenuItem(0L, "Milkshake", BigDecimal.valueOf(5), "https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=500&q=80"),
                        new MenuItem(0L, "Onion Rings", BigDecimal.valueOf(4.5), "https://images.unsplash.com/photo-1639024471283-03518883512d?w=500&q=80")
                )
        );

        restaurantRepository.saveAll(List.of(mumbaiSpice, bangaloreBytes, nycPizza, laBurgers));
    }

    private void seedPaymentMethods() {
        paymentMethodRepository.save(new PaymentMethod(0L, "CARD", "**** **** **** 4242"));
    }
}
