package com.sprBoot.order.service;

import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sprBoot.order.Exception.ResourceNotFoundException;
import com.sprBoot.order.Exception.ServiceUnavailableException;
import com.sprBoot.order.entity.Order;
import com.sprBoot.order.repo.OrderRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class OrderService {
	 private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(OrderService.class);
	
    private final OrderRepository orderRepository;
	private final RestTemplate restTemplate; 
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    
    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate,
			CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
		super();
		this.orderRepository = orderRepository;
		this.restTemplate = restTemplate;
		this.circuitBreakerFactory = circuitBreakerFactory;
	}

	/*
	 * @CircuitBreaker(name = "orderService", fallbackMethod =
	 * "createOrderFallback") public Order createOrder(Order order) { return
	 * circuitBreakerFactory.create("orderService").run( () -> { // Original logic
	 * String userServiceUrl = "http://user-service/api/users/" + order.getUserId();
	 * restTemplate.getForEntity(userServiceUrl, Object.class); return
	 * orderRepository.save(order); }, throwable -> createOrderFallback(order,
	 * throwable) ); }
	 */

    @CircuitBreaker(name = "orderService", fallbackMethod = "createOrderFallback")
    public Order createOrder(Order order) {
        log.debug("Initiating order creation for user ID: {}", order.getUserId());
        
        // Verify user existence
        log.info("Calling User Service for ID: {}", order.getUserId());
        ResponseEntity<Object> response = restTemplate.getForEntity(
            "http://localhost:8081/api/users/" + order.getUserId(), 
            Object.class
        );
        
        if(!response.getStatusCode().is2xxSuccessful()) {
            log.error("User validation failed for ID: {}", order.getUserId());
            throw new ResourceNotFoundException("User not found with ID: " + order.getUserId());
        }
        
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully. Order ID: {}", savedOrder.getId());
        return savedOrder;
    }
    
    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public Page<Order> getOrdersByUserId(Long userId, int page, int size) {
        return orderRepository.findByUserId(userId, PageRequest.of(page, size));
    }

    public Order updateOrder(Long id, Order orderDetails) {
        Order order = getOrderById(id);
        order.setProduct(orderDetails.getProduct());
        order.setQuantity(orderDetails.getQuantity());
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        Order order = getOrderById(id);
        orderRepository.delete(order);
    }

    // Fallback method
    private Order createOrderFallback(Order order, Throwable t) {
        throw new ServiceUnavailableException("User service is currently unavailable");
    }
}