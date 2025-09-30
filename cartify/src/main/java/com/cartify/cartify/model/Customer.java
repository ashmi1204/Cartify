package com.cartify.cartify.model;

import jakarta.persistence.*;

@Entity
@Table(name="customers")
public class Customer {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int customerId;

    private String customerName;
    private String email;
    private String address;
    private int mobileNo;
    private String password;

    // Getters and Setters
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public int getMobileNo() { return mobileNo; }
    public void setMobileNo(int mobileNo) { this.mobileNo = mobileNo; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
