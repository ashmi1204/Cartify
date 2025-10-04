package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepo customerRepository;

    @Autowired
    public CustomerService(CustomerRepo customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Map<String, Object> registerCustomer(Customer customer) {
        Map<String, Object> response = new HashMap<>();
        if (customerRepository.existsByEmail(customer.getEmail())) {
            response.put("success", false);
            response.put("message", "Email already registered.");
            return response;
        }
        // WARNING: Storing password in plain text. Use a password encoder in production.
        customerRepository.save(customer);
        response.put("success", true);
        response.put("message", "Customer registered successfully.");
        return response;
    }

    public Map<String, Object> loginCustomer(String email, String rawPassword) {
        Map<String, Object> response = new HashMap<>();
        Optional<Customer> customerOpt = customerRepository.findByEmail(email);

        if (customerOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Login failed: Email not found.");
            return response;
        }

        Customer customer = customerOpt.get();
        // WARNING: Comparing plain text passwords.
        if (customer.getPassword().equals(rawPassword)) {
            response.put("success", true);
            response.put("message", "Login successful.");
            response.put("customer_id", customer.getCustomer_id());
        } else {
            response.put("success", false);
            response.put("message", "Login failed: Incorrect password.");
        }
        return response;
    }
}