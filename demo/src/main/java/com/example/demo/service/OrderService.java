package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.model.Order;
import com.example.demo.model.OrderItem;
import com.example.demo.model.Product;
import com.example.demo.model.dto.OrderDetailsDTO;
import com.example.demo.model.dto.OrderRequestDTO;
import com.example.demo.repository.CustomerRepo;
import com.example.demo.repository.OrderItemRepo;
import com.example.demo.repository.OrderRepo;
import com.example.demo.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepo orderRepository;
    private final OrderItemRepo orderItemRepository;
    private final ProductRepo productRepository;
    private final CustomerRepo customerRepository;

    @Autowired
    public OrderService(OrderRepo orderRepository, OrderItemRepo orderItemRepository, ProductRepo productRepository, CustomerRepo customerRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional // Ensures all database operations succeed or none do
    public Order createOrder(OrderRequestDTO orderRequest) {
        // 1. Calculate total amount
        double total = orderRequest.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        // 2. Create and save the main order record
        Order order = new Order();
        order.setCustomer_id(orderRequest.getCustomerId());
        order.setOrder_date(LocalDateTime.now());
        order.setOrder_status("PENDING");
        order.setTotal_amount(total);
        Order savedOrder = orderRepository.save(order);

        // 3. Create and save each order item and update product stock
        for (OrderRequestDTO.OrderItemDTO itemDTO : orderRequest.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder_id(savedOrder.getOrder_id());
            orderItem.setProduct_id(itemDTO.getProductId());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnit_price(itemDTO.getPrice());
            orderItemRepository.save(orderItem);

            // 4. Update product stock
            productRepository.updateStock(itemDTO.getProductId(), itemDTO.getQuantity());
        }

        return savedOrder;
    }

    public Optional<OrderDetailsDTO> getOrderDetailsById(Long id) {
        Optional<Order> orderOpt = orderRepository.findById(id);
        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(buildOrderDetailsDTO(orderOpt.get()));
    }

    public List<OrderDetailsDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::buildOrderDetailsDTO)
                .collect(Collectors.toList());
    }

    public void updateOrderStatus(Long orderId, String status) {
        orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        orderRepository.updateStatus(orderId, status);
    }

    // Helper method to build the rich DTO for API responses
    private OrderDetailsDTO buildOrderDetailsDTO(Order order) {
        Customer customer = customerRepository.findById(order.getCustomer_id())
                .orElse(new Customer()); // Default empty customer if not found

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getOrder_id());

        List<OrderDetailsDTO.OrderItemDetailDTO> itemDetails = items.stream().map(item -> {
            Product product = productRepository.findById(item.getProduct_id())
                    .orElse(new Product()); // Default empty product
            return new OrderDetailsDTO.OrderItemDetailDTO(product.getName(), item.getQuantity(), item.getUnit_price());
        }).collect(Collectors.toList());

        OrderDetailsDTO dto = new OrderDetailsDTO();
        dto.setId(order.getOrder_id());
        dto.setCustomerName(customer.getCustomer_name());
        dto.setCustomerEmail(customer.getEmail());
        dto.setOrderDate(order.getOrder_date());
        dto.setStatus(order.getOrder_status());
        dto.setTotalAmount(order.getTotal_amount());
        dto.setItems(itemDetails);

        return dto;
    }
}