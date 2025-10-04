package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepo {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Order> orderRowMapper = (rs, rowNum) -> {
        Order order = new Order();
        order.setOrder_id(rs.getLong("order_id"));
        order.setCustomer_id(rs.getLong("customer_id"));
        order.setOrder_date(rs.getTimestamp("order_date").toLocalDateTime());
        order.setOrder_status(rs.getString("order_status"));
        order.setTotal_amount(rs.getDouble("total_amount"));
        return order;
    };

    @Autowired
    public OrderRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Order save(Order order) {
        final String sql = "INSERT INTO orders (customer_id, order_date, order_status, total_amount) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, order.getCustomer_id());
            ps.setTimestamp(2, Timestamp.valueOf(order.getOrder_date()));
            ps.setString(3, order.getOrder_status());
            ps.setDouble(4, order.getTotal_amount());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            order.setOrder_id(keyHolder.getKey().longValue());
        }
        return order;
    }

    public Optional<Order> findById(Long id) {
        final String sql = "SELECT * FROM orders WHERE order_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, orderRowMapper, id));
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Order> findByCustomerId(Long customerId) {
        final String sql = "SELECT * FROM orders WHERE customer_id = ?";
        return jdbcTemplate.query(sql, orderRowMapper, customerId);
    }

    public List<Order> findAll() {
        final String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        return jdbcTemplate.query(sql, orderRowMapper);
    }

    public void updateStatus(Long orderId, String status) {
        final String sql = "UPDATE orders SET order_status = ? WHERE order_id = ?";
        jdbcTemplate.update(sql, status, orderId);
    }
}