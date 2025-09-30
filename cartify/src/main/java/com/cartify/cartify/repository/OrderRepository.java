package com.cartify.cartify.repository;

import com.cartify.cartify.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomerCustomerId(int customerId);
}
