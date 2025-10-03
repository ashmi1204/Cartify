package com.example.demo.repository;

import com.example.demo.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepo {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper maps database columns to the Order object fields
    private final RowMapper<Order> orderRowMapper = new RowMapper<Order>() {
        @Override
        public Order mapRow(ResultSet rs, int rowNum) throws SQLException {
            Order order = new Order();
            order.setOrder_id(rs.getLong("order_id"));
            // JDBC handles LocalDateTime mapping for standard timestamp columns
            order.setOrder_date(rs.getTimestamp("order_date").toLocalDateTime());
            order.setOrder_status(rs.getString("order_status"));
            order.setCustomer_id(rs.getLong("customer_id"));
            return order;
        }
    };

    @Autowired
    public OrderRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Saves a new order and retrieves the auto-generated ID.
     */
    public Order save(Order order) {
        final String sql = "INSERT INTO orders (order_date, order_status, customer_id) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // 1. Ensure order_date is set before saving (if not set in the service/controller)
        if (order.getOrder_date() == null) {
            order.setOrder_date(LocalDateTime.now());
        }

        // 2. Execute update and retrieve generated ID
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(order.getOrder_date()));
            ps.setString(2, order.getOrder_status());
            ps.setLong(3, order.getCustomer_id());
            return ps;
        }, keyHolder);

        // 3. Set the generated ID back onto the Order object
        if (keyHolder.getKey() != null) {
            order.setOrder_id(keyHolder.getKey().longValue());
        }
        return order;
    }

    /**
     * Finds a single order by its ID.
     */
    public Optional<Order> findById(Long id) {
        final String sql = "SELECT * FROM orders WHERE order_id = ?";
        try {
            Order order = jdbcTemplate.queryForObject(sql, orderRowMapper, id);
            return Optional.ofNullable(order);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Finds all orders placed by a specific customer ID.
     */
    public List<Order> findByCustomerId(Long customerId) {
        final String sql = "SELECT * FROM orders WHERE customer_id = ?";
        return jdbcTemplate.query(sql, orderRowMapper, customerId);
    }
}