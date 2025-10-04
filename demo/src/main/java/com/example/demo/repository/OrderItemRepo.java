package com.example.demo.repository;

import com.example.demo.model.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderItemRepo {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<OrderItem> rowMapper = (rs, rowNum) -> {
        OrderItem item = new OrderItem();
        item.setOrder_item_id(rs.getLong("order_item_id"));
        item.setOrder_id(rs.getLong("order_id"));
        item.setProduct_id(rs.getLong("product_id"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnit_price(rs.getDouble("unit_price"));
        return item;
    };

    public OrderItemRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(OrderItem item) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, item.getOrder_id(), item.getProduct_id(), item.getQuantity(), item.getUnit_price());
    }

    public List<OrderItem> findByOrderId(Long orderId) {
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        return jdbcTemplate.query(sql, rowMapper, orderId);
    }
}