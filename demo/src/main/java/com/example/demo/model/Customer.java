package com.example.demo.model;

// NO JPA IMPORTS NEEDED (NO @Entity, @Table)

public class Customer {

    private Long customer_id;
    private String customer_name;
    private String email;
    private String address;
    private String mobile_no;
    private String password;

    // --- Constructors ---
    public Customer() {}

    // --- Getters and Setters (REQUIRED) ---

    public Long getCustomer_id() {
        return customer_id;
    }
    public void setCustomer_id(Long customer_id) {
        this.customer_id = customer_id;
    }
    public String getCustomer_name() {
        return customer_name;
    }
    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getMobile_no() {
        return mobile_no;
    }
    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}