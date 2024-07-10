package com.assessment.java.service;

import com.assessment.java.model.Order;
import com.assessment.java.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderService orderService;


    @Test
    void testPlaceOrderShouldReturnTrueWhenPaymentHasBeenProcessed() {

        doNothing().when(orderRepository).save(Mockito.<Order>any());
        when(paymentService.processPayment(anyDouble())).thenReturn(true);
        // Act
        var order = getOrderObject();
        boolean actualPlaceOrderResult = orderService.placeOrder(order);

        // Assert
        verify(orderRepository).save(isA(Order.class));
        verify(paymentService).processPayment(10.0d);
        verify(orderRepository, times(1)).save(Mockito.<Order>any());
        verify(paymentService, times(1)).processPayment(anyDouble());
        assertTrue(actualPlaceOrderResult);
    }

    @Test
    void testPlaceOrderShouldThrowIllegalArgumentExceptionWhenSaveOrder() {

        doThrow(new IllegalArgumentException("Ilegal argument")).when(orderRepository).save(Mockito.<Order>any());
        when(paymentService.processPayment(anyDouble())).thenReturn(true);

        var order = getOrderObject();
        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> orderService.placeOrder(order));
        verify(orderRepository).save(isA(Order.class));
        verify(paymentService).processPayment(10.0d);
        verify(orderRepository, times(1)).save(Mockito.<Order>any());
        verify(paymentService, times(1)).processPayment(anyDouble());
    }

    @Test
    void testPlaceOrderShouldReturnFalseWhenOrderHasNotBeenProcessed() {
        // Arrange
        when(paymentService.processPayment(anyDouble())).thenReturn(false);
        var order = getOrderObject();
        // Act
        boolean actualPlaceOrderResult = orderService.placeOrder(order);

        // Assert
        verify(paymentService).processPayment(10.0d);
        assertFalse(actualPlaceOrderResult);
        verify(paymentService, times(1)).processPayment(anyDouble());
    }

    @Test
    void testGetOrderByIdShouldReturnOrderWhenIdIsValid() {
        // Arrange
        var order = getOrderObject();

        Optional<Order> ofResult = Optional.of(order);
        when(orderRepository.findById(anyInt())).thenReturn(ofResult);

        // Act
        Order actualOrderById = orderService.getOrderById(1);

        // Assert
        verify(orderRepository).findById(1);
        assertSame(order, actualOrderById);
        verify(orderRepository, times(1)).findById(anyInt());
    }


    @Test
    void testGetOrderByIdThrowExceptionWhenOrderNotFound() {
        // Arrange
        when(orderRepository.findById(anyInt())).thenThrow(new IllegalArgumentException("test"));

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> orderService.getOrderById(1));
        verify(orderRepository).findById(1);
    }

    @Test
    void testCancelOrderShouldCancelAnOrder() {
        // Arrange
        Order order = new Order();
        order.setAmount(10.0d);
        order.setId(1);
        Optional<Order> ofResult = Optional.of(order);
        doNothing().when(orderRepository).delete(Mockito.<Order>any());
        when(orderRepository.findById(anyInt())).thenReturn(ofResult);

        // Act
        orderService.cancelOrder(1);

        // Assert
        verify(orderRepository).delete(isA(Order.class));
        verify(orderRepository).findById(1);
    }


    @Test
    void testCancelOrderThrowExceptionWhenDeleteOrderInRepository() {
        // Arrange
        Order order = new Order();
        order.setAmount(10.0d);
        order.setId(1);
        Optional<Order> ofResult = Optional.of(order);

        doThrow(new IllegalArgumentException("test")).when(orderRepository).delete(Mockito.<Order>any());
        when(orderRepository.findById(anyInt())).thenReturn(ofResult);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> orderService.cancelOrder(1));
        verify(orderRepository).delete(isA(Order.class));
        verify(orderRepository).findById(1);
    }

    @Test
    void testCancelOrderThrowExceptionWhenFindByIdReturnsEmptyResult() {
        // Arrange
        Optional<Order> emptyResult = Optional.empty();
        when(orderRepository.findById(anyInt())).thenReturn(emptyResult);

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> orderService.cancelOrder(1));
        verify(orderRepository).findById(1);
    }


    @Test
    void testListAllOrders() {
        // Arrange
        ArrayList<Order> orderList = new ArrayList<>();
        when(orderRepository.findAll()).thenReturn(orderList);

        // Act
        List<Order> actualListAllOrdersResult = (new OrderService(orderRepository, paymentService))
                .listAllOrders();

        // Assert
        verify(orderRepository).findAll();
        assertTrue(actualListAllOrdersResult.isEmpty());
        assertSame(orderList, actualListAllOrdersResult);
    }

    @Test
    void testListAllOrdersThrowAnExceptionWhenFindAllDoesNotReturnResults() {
        // Arrange
        when(orderRepository.findAll()).thenThrow(new IllegalArgumentException("foo"));

        // Act and Assert
        assertThrows(IllegalArgumentException.class, () -> orderService.listAllOrders());
        verify(orderRepository).findAll();
    }

    private Order getOrderObject(){
        Order order = new Order();
        order.setAmount(10.0d);
        order.setId(1);
        return order;
    }
}
