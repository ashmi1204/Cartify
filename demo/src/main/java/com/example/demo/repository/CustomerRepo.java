package com.example.demo.repository;

import com.example.demo.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

@Repository
public class CustomerRepo {

    private final JdbcTemplate jdbcTemplate;

    // RowMapper maps database columns to the Customer object fields
    private final RowMapper<Customer> customerRowMapper = new RowMapper<Customer>() {
        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer customer = new Customer();
            customer.setCustomer_id(rs.getLong("customer_id"));
            customer.setCustomer_name(rs.getString("customer_name"));
            customer.setEmail(rs.getString("email"));
            customer.setAddress(rs.getString("address"));

            // ✅ FIX 1: Ensure RowMapper reads mobile_no as a String (VARCHAR)
            customer.setMobile_no(rs.getString("mobile_no"));

            customer.setPassword(rs.getString("password"));
            return customer;
        }
    };

    @Autowired
    public CustomerRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Saves a new customer and retrieves the auto-generated ID.
     */
    public Customer save(Customer customer) {
        final String sql = "INSERT INTO customers (customer_name, email, address, mobile_no, password) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getCustomer_name());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getAddress());

            // ✅ FIX 2: Ensure PreparedStatement binds mobile_no as a String (VARCHAR)
            ps.setString(4, customer.getMobile_no());

            ps.setString(5, customer.getPassword());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            customer.setCustomer_id(keyHolder.getKey().longValue());
        }
        return customer;
    }

    /**
     * Finds a customer by their unique email address (for login).
     */
    public Optional<Customer> findByEmail(String email) {
        final String sql = "SELECT * FROM customers WHERE email = ?";
        try {
            Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper, email);
            return Optional.ofNullable(customer);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    /**
     * Checks if a customer with the given email already exists (for registration validation).
     */
    public boolean existsByEmail(String email) {
        // Simple query to count matching rows
        String sql = "SELECT count(*) FROM customers WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
}