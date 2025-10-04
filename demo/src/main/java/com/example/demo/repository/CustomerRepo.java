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

    private final RowMapper<Customer> customerRowMapper = (rs, rowNum) -> {
        Customer customer = new Customer();
        customer.setCustomer_id(rs.getLong("customer_id"));
        customer.setCustomer_name(rs.getString("customer_name"));
        customer.setEmail(rs.getString("email"));
        customer.setAddress(rs.getString("address"));
        customer.setMobile_no(rs.getString("mobile_no"));
        customer.setPassword(rs.getString("password"));
        return customer;
    };

    @Autowired
    public CustomerRepo(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Customer save(Customer customer) {
        final String sql = "INSERT INTO customers (customer_name, email, address, mobile_no, password) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, customer.getCustomer_name());
            ps.setString(2, customer.getEmail());
            ps.setString(3, customer.getAddress());
            ps.setString(4, customer.getMobile_no());
            ps.setString(5, customer.getPassword());
            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            customer.setCustomer_id(keyHolder.getKey().longValue());
        }
        return customer;
    }

    public Optional<Customer> findByEmail(String email) {
        final String sql = "SELECT * FROM customers WHERE email = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, customerRowMapper, email));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Optional<Customer> findById(Long id) {
        final String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, customerRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean existsByEmail(String email) {
        String sql = "SELECT count(*) FROM customers WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
}