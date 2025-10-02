package com.cartfy.userapi.controller;

import com.cartfy.userapi.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    // In-memory user store (later you can replace with DB)
    private Map<String, User> users = new HashMap<>();

    // REGISTER
    @PostMapping("/register")
    public String register(@RequestBody User user) {
        if (users.containsKey(user.getEmail())) {
            return "❌ User already exists!";
        }
        users.put(user.getEmail(), user);
        return "✅ Registered successfully for " + user.getName();
    }

    // LOGIN
    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        User user = users.get(email);
        if (user == null) {
            return "❌ User not found!";
        }
        if (!user.getPassword().equals(password)) {
            return "❌ Incorrect password!";
        }
        return "✅ Login successful. Welcome " + user.getName();
    }
}
