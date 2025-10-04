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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepo orderRepository;
    private final OrderItemRepo orderItemRepository;
    private final ProductRepo productRepository;
    private final CustomerRepo customerRepository;

    @Autowired
    public OrderService(OrderRepo orderRepository, OrderItemRepo orderItemRepository,
                        ProductRepo productRepository, CustomerRepo customerRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Order createOrder(OrderRequestDTO orderRequest) {
        log.info("Creating order for customer ID: {}", orderRequest.getCustomerId());

        // 1. Validate customer exists
        Customer customer = customerRepository.findById(orderRequest.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer not found with ID: " + orderRequest.getCustomerId()));
        log.info("Customer found: {}", customer.getCustomer_name());

        // 2. Validate products and stock availability
        for (OrderRequestDTO.OrderItemDTO itemDTO : orderRequest.getItems()) {
            Product product = productRepository.findById(itemDTO.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Product not found with ID: " + itemDTO.getProductId()));

            log.info("Checking product: {} (Available: {}, Requested: {})",
                    product.getName(), product.getQuantity(), itemDTO.getQuantity());

            if (product.getQuantity() < itemDTO.getQuantity()) {
                throw new IllegalStateException(
                        "Insufficient stock for product: " + product.getName() +
                                ". Available: " + product.getQuantity() +
                                ", Requested: " + itemDTO.getQuantity());
            }
        }

        // 3. Calculate total amount
        double total = orderRequest.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();
        log.info("Order total calculated: ${}", total);

        // 4. Create and save the main order record
        Order order = new Order();
        order.setCustomer_id(orderRequest.getCustomerId());
        order.setOrder_date(LocalDateTime.now());
        order.setOrder_status("PENDING");
        order.setTotal_amount(total);
        Order savedOrder = orderRepository.save(order);
        log.info("Order created with ID: {}", savedOrder.getOrder_id());

        // 5. Create order items and update stock
        for (OrderRequestDTO.OrderItemDTO itemDTO : orderRequest.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder_id(savedOrder.getOrder_id());
            orderItem.setProduct_id(itemDTO.getProductId());
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setUnit_price(itemDTO.getPrice());
            orderItemRepository.save(orderItem);
            log.info("Order item saved: Product {}, Quantity {}",
                    itemDTO.getProductId(), itemDTO.getQuantity());

            // Update product stock
            productRepository.updateStock(itemDTO.getProductId(), itemDTO.getQuantity());
            log.info("Stock updated for product ID: {}", itemDTO.getProductId());
        }

        log.info("Order completed successfully: {}", savedOrder.getOrder_id());
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

    private OrderDetailsDTO buildOrderDetailsDTO(Order order) {
        Customer customer = customerRepository.findById(order.getCustomer_id())
                .orElse(new Customer());

        List<OrderItem> items = orderItemRepository.findByOrderId(order.getOrder_id());

        List<OrderDetailsDTO.OrderItemDetailDTO> itemDetails = items.stream().map(item -> {
            Product product = productRepository.findById(item.getProduct_id())
                    .orElse(new Product());
            return new OrderDetailsDTO.OrderItemDetailDTO(
                    product.getName(), item.getQuantity(), item.getUnit_price());
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