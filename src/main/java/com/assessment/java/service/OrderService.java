package com.assessment.java.service;

import com.assessment.java.model.Order;
import com.assessment.java.repository.OrderRepository;

import java.util.List;

public class OrderService {

    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository, PaymentService
            paymentService) {
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
    }
    public boolean placeOrder(Order order) {
        boolean paymentProcessed =
                paymentService.processPayment(order.getAmount());
        if (paymentProcessed) {
            orderRepository.save(order);
            return true;
        }
        return false;
    }
    public Order getOrderById(int id) {
        return orderRepository.findById(id).orElse(null);
    }
    public void cancelOrder(int id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
                        orderRepository.delete(order);
    }
    public List<Order> listAllOrders() {
        return orderRepository.findAll();
    }
}
