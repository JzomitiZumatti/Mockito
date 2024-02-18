package com.example.glovodb.service;

import com.example.glovodb.entity.Order;
import com.example.glovodb.entity.Product;
import com.example.glovodb.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {
    @Mock
    public OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    private final int TEST_ID = 1;

    @Test
    public void createOrderTest() {
        Order initialOrder = Order.builder().id(TEST_ID).name("Bob").build();
        when(orderRepository.save(any())).thenReturn(initialOrder);
        Order actualOrder = orderService.createOrder(new Order()).getBody();
        assertEquals(initialOrder, actualOrder);
    }

    @Test
    public void getOrderByIdTest() {
        Order initialOrder = Order.builder().id(TEST_ID).name("Bob").build();
        when(orderRepository.findById(any())).thenReturn(Optional.of(initialOrder));
        Order actualOrder = orderService.getOrderById(anyInt()).getBody();
        assertEquals(initialOrder, actualOrder);
    }

    @Test
    public void getOrderByIdWhenIdNotFoundTest() {
        when(orderRepository.findById(anyInt())).thenReturn(Optional.empty());
        ResponseEntity<Order> response = orderService.getOrderById(TEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
    @Test
    public void updateOrderTest() {
        Order initialOrder = Order.builder()
                .id(TEST_ID)
                .name("Bob")
                .customerPhoneNumber("+123456")
                .address("Olenivska str. 27")
                .products(List.of(new Product()))
                .build();
        when(orderRepository.findById(any())).thenReturn(Optional.of(initialOrder));
        Order actualOrder = orderService.updateOrder(anyInt(), initialOrder).getBody();
        assertEquals(initialOrder, actualOrder);
    }

    @Test
    public void updateOrderWhenOrderNotFoundTest() {
        Order initialOrder = Order.builder().id(TEST_ID).build();
        when(orderRepository.findById(anyInt())).thenReturn(Optional.empty());
        ResponseEntity<Order> response = orderService.updateOrder(TEST_ID, initialOrder);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void addProductToOrderTest() {
        Collection<Product> products = new ArrayList<>();
        products.add(new Product());
        Order initialOrder = Order.builder()
                .id(TEST_ID)
                .name("Bob")
                .customerPhoneNumber("+123456")
                .address("Olenivska str. 27")
                .products(products)
                .build();
        when(orderRepository.findById(anyInt())).thenReturn(Optional.of(initialOrder));
        Order actualOrder = orderService.addProductToOrder(anyInt(), new Product()).getBody();
        assertEquals(initialOrder, actualOrder);
    }

    @Test
    public void addProductToOrderWhenOrderNotFoundTest() {
        Product product = Product.builder().id(TEST_ID).build();
        when(orderRepository.findById(any())).thenReturn(Optional.empty());
        ResponseEntity<Order> response = orderService.addProductToOrder(anyInt(), product);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void deleteProductFromOrderTest() {
        Collection<Product> products = new ArrayList<>();
        Product product = Product.builder().id(TEST_ID).build();
        products.add(product);
        Order initialOrder = Order.builder()
                .id(TEST_ID)
                .name("Bob")
                .customerPhoneNumber("+123456")
                .address("Olenivska str. 27")
                .products(products)
                .build();
        Mockito.when(orderRepository.findById(TEST_ID)).thenReturn(Optional.of(initialOrder));
        Mockito.when(orderRepository.save(initialOrder)).thenReturn(initialOrder);
        ResponseEntity<Order> response = orderService.deleteProductFromOrder(initialOrder.getId(), product.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, Objects.requireNonNull(response.getBody()).getProducts().size());
    }

    @Test
    public void deleteProductFromOrderWhenOrderNotFoundTest() {
        when(orderRepository.findById(TEST_ID)).thenReturn(Optional.empty());
        ResponseEntity<Order> response = orderService.deleteProductFromOrder(TEST_ID, TEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void deleteProductFromOrderWhenProductNotFoundTest() {
        Order initialOrder = Order.builder().id(TEST_ID).products(new ArrayList<>()).build();
        when(orderRepository.findById(initialOrder.getId())).thenReturn(Optional.of(initialOrder));
        ResponseEntity<Order> response = orderService.deleteProductFromOrder(initialOrder.getId(), TEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void deleteOrderByIdTest() {
        Order beforeUpdate = Order.builder()
                .id(TEST_ID)
                .name("Bob")
                .customerPhoneNumber("+123456")
                .address("Olenivska str. 27")
                .products(List.of(new Product()))
                .build();
        when(orderRepository.findById(any())).thenReturn(Optional.of(beforeUpdate));
        ResponseEntity<Order> response = orderService.deleteOrderById(TEST_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(orderRepository, Mockito.times(1)).deleteById(TEST_ID);
    }

    @Test
    public void deleteOrderByIdWhenOrderNotFoundTest() {
        Mockito.when(orderRepository.findById(TEST_ID)).thenReturn(Optional.empty());
        ResponseEntity<Order> response = orderService.deleteOrderById(TEST_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Mockito.verify(orderRepository, Mockito.never()).deleteById(anyInt());
    }
}
