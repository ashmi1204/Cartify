package com.example.demo.service;

import com.example.demo.model.Customer;
import com.example.demo.repository.CustomerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// ❌ REMOVED: import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepo customerRepository;
    // ❌ REMOVED: private final PasswordEncoder passwordEncoder;

    @Autowired
    // ❌ REMOVED PasswordEncoder from the constructor
    public CustomerService(CustomerRepo customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Handles customer registration logic.
     */
    public Map<String, Object> registerCustomer(Customer customer) {
        Map<String, Object> response = new HashMap<>();

        // 1. Validation
        if (customerRepository.existsByEmail(customer.getEmail())) {
            response.put("success", false);
            response.put("message", "Email already registered.");
            return response;
        }

        // 2. Hash the password
        // 🚨 WARNING: Password is saved as plain text!
        // ❌ REMOVED HASHING LOGIC
        // customer.setPassword(customer.getPassword()); // Already plain text

        // 3. Save the customer
        customerRepository.save(customer);

        response.put("success", true);
        response.put("message", "Customer registered successfully.");
        return response;
    }

    /**
     * Handles customer login logic.
     */
    public Map<String, Object> loginCustomer(String email, String rawPassword) {
        Map<String, Object> response = new HashMap<>();

        Optional<Customer> customerOpt = customerRepository.findByEmail(email);

        if (customerOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Login failed: Email not found.");
            return response;
        }

        Customer customer = customerOpt.get();

        // 4. Check if the raw password matches the database password
        // 🚨 WARNING: Using INSECURE plain text comparison
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