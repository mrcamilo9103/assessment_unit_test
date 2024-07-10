package com.assessment.java.repository;

import com.assessment.java.model.Order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {

    Optional<Order> findById(int id);
    void save(Order order);
    void delete(Order order);
    List<Order> findAll();
}
