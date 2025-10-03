package com.cartify;

import javax.swing.JOptionPane;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MockSellerService {
    private List<Product> products;
    private List<Order> orders;
    private int productIdCounter = 1;
    private int orderIdCounter = 1;

    public MockSellerService() {
        this.products = new ArrayList<>();
        this.orders = new ArrayList<>();
        initializeMockData();
    }

    private void initializeMockData() {
        System.out.println("🛠️ Initializing mock data...");

        // Electronics
        products.add(new Product(productIdCounter++, "MacBook Pro", "16-inch MacBook Pro with M3 Pro chip, 18GB RAM, 1TB SSD", 2399.99, 8, "Electronics", "seller1"));
        products.add(new Product(productIdCounter++, "iPhone 15", "Latest iPhone with A17 Pro chip, 128GB Storage, Titanium Design", 999.99, 25, "Electronics", "seller1"));
        products.add(new Product(productIdCounter++, "SamsungGalaxy S24", "6.7-inch AMOLED, 256GB, Advanced AI Features", 849.99, 18, "Electronics", "seller1"));
        products.add(new Product(productIdCounter++, "Sony WH-1000XM5", "Industry leading noise cancellation wireless headphones", 349.99, 30, "Electronics", "seller1"));
        products.add(new Product(productIdCounter++, "iPad Air", "10.9-inch Liquid Retina display, M1 chip, 64GB", 599.99, 12, "Electronics", "seller1"));
        products.add(new Product(productIdCounter++, "Apple Watch Series 9", "45mm GPS + Cellular, Aluminum Case", 429.99, 22, "Electronics", "seller1"));
        products.add(new Product(productIdCounter++, "Dell XPS 13", "13.4-inch FHD+ InfinityEdge Touch Laptop", 1199.99, 10, "Electronics", "seller1"));
        products.add(new Product(productIdCounter++, "PlayStation 5", "Ultra-high speed SSD, 4K gaming, DualSense wireless controller", 499.99, 5, "Electronics", "seller1"));
        products.add(new Product(productIdCounter++, "Nintendo Switch OLED", "7-inch OLED screen, enhanced audio, 64GB internal storage", 349.99, 15, "Electronics", "seller1"));
        products.add(new Product(productIdCounter++, "Bose SoundLink", "Bluetooth speaker with 12-hour battery life", 129.99, 40, "Electronics", "seller1"));

        // Computer Accessories
        products.add(new Product(productIdCounter++, "Mechanical Keyboard", "RGB mechanical keyboard with blue switches, USB-C", 129.99, 30, "Electronics", "seller1"));
        products.add(new Product(productIdCounter++, "Gaming Mouse", "High-precision gaming mouse with customizable RGB lighting", 79.99, 45, "Electronics", "seller1"));
        products.add(new Product(productIdCounter++, "4K Monitor", "27-inch 4K UHD LED monitor with HDR10", 349.99, 15, "Electronics", "seller1"));
        products.add(new Product(productIdCounter++, "Wireless Earbuds", "True wireless earbuds with active noise cancellation", 199.99, 35, "Electronics", "seller1"));
        products.add(new Product(productIdCounter++, "Webcam", "4K webcam with autofocus and built-in microphone", 89.99, 25, "Electronics", "seller1"));
        products.add(new Product(productIdCounter++, "External SSD", "1TB USB-C portable solid state drive", 129.99, 20, "Electronics", "seller1"));

        // Furniture
        products.add(new Product(productIdCounter++, "Gaming Chair", "Ergonomic gaming chair with lumbar support and headrest", 299.99, 12, "Furniture", "seller1"));
        products.add(new Product(productIdCounter++, "Office Desk", "Modern standing desk with adjustable height, 55x28 inches", 499.99, 8, "Furniture", "seller1"));
        products.add(new Product(productIdCounter++, "Bookshelf", "5-tier wooden bookshelf with sturdy construction", 89.99, 20, "Furniture", "seller1"));
        products.add(new Product(productIdCounter++, "Office Chair", "Ergonomic office chair with mesh back and adjustable arms", 189.99, 18, "Furniture", "seller1"));
        products.add(new Product(productIdCounter++, "Computer Desk", "L-shaped corner desk with monitor stand and storage", 199.99, 10, "Furniture", "seller1"));
        products.add(new Product(productIdCounter++, "Filing Cabinet", "2-drawer letter size filing cabinet with lock", 79.99, 15, "Furniture", "seller1"));

        // Home & Kitchen
        products.add(new Product(productIdCounter++, "Air Fryer", "Digital air fryer with 6-quart capacity, 8 presets", 89.99, 25, "Home & Kitchen", "seller1"));
        products.add(new Product(productIdCounter++, "Blender", "High-speed blender with 64oz glass jar, 1500W motor", 129.99, 20, "Home & Kitchen", "seller1"));
        products.add(new Product(productIdCounter++, "Coffee Maker", "Programmable coffee maker with thermal carafe, 12-cup", 79.99, 30, "Home & Kitchen", "seller1"));
        products.add(new Product(productIdCounter++, "Robot Vacuum", "Smart robot vacuum with mapping and app control", 299.99, 12, "Home & Kitchen", "seller1"));
        products.add(new Product(productIdCounter++, "Stand Mixer", "5-quart stand mixer with 10 speeds and attachments", 329.99, 8, "Home & Kitchen", "seller1"));

        // Clothing
        products.add(new Product(productIdCounter++, "Running Shoes", "Lightweight running shoes with cushioning technology", 89.99, 50, "Clothing", "seller1"));
        products.add(new Product(productIdCounter++, "Winter Jacket", "Waterproof winter jacket with insulation, multiple sizes", 149.99, 25, "Clothing", "seller1"));
        products.add(new Product(productIdCounter++, "Jeans", "Slim fit jeans with stretch, various sizes and colors", 49.99, 60, "Clothing", "seller1"));
        products.add(new Product(productIdCounter++, "T-Shirt Pack", "Pack of 5 cotton t-shirts, assorted colors", 29.99, 100, "Clothing", "seller1"));

        // Sports & Outdoors
        products.add(new Product(productIdCounter++, "Yoga Mat", "Non-slip yoga mat with carrying strap, 6mm thickness", 24.99, 40, "Sports & Outdoors", "seller1"));
        products.add(new Product(productIdCounter++, "Dumbbell Set", "Adjustable dumbbell set with case, 40lb total", 99.99, 15, "Sports & Outdoors", "seller1"));
        products.add(new Product(productIdCounter++, "Bicycle", "Mountain bike with 21 speeds and front suspension", 299.99, 8, "Sports & Outdoors", "seller1"));
        products.add(new Product(productIdCounter++, "Tent", "4-person camping tent with rainfly and storage pockets", 129.99, 12, "Sports & Outdoors", "seller1"));

        // Sample orders with realistic data
        Order order1 = new Order(orderIdCounter++, "John Smith", "john.smith@email.com", "PENDING");
        order1.addItem(new OrderItem(1, "MacBook Pro", 1, 2399.99));
        order1.addItem(new OrderItem(11, "Mechanical Keyboard", 1, 129.99));
        order1.addItem(new OrderItem(12, "Gaming Mouse", 1, 79.99));
        orders.add(order1);

        Order order2 = new Order(orderIdCounter++, "Emily Johnson", "emily.johnson@email.com", "CONFIRMED");
        order2.addItem(new OrderItem(2, "iPhone 15", 2, 999.99));
        order2.addItem(new OrderItem(14, "Wireless Earbuds", 1, 199.99));
        orders.add(order2);

        Order order3 = new Order(orderIdCounter++, "Michael Brown", "michael.brown@email.com", "SHIPPED");
        order3.addItem(new OrderItem(17, "Gaming Chair", 1, 299.99));
        order3.addItem(new OrderItem(18, "Office Desk", 1, 499.99));
        order3.addItem(new OrderItem(13, "4K Monitor", 2, 349.99));
        orders.add(order3);

        Order order4 = new Order(orderIdCounter++, "Sarah Davis", "sarah.davis@email.com", "DELIVERED");
        order4.addItem(new OrderItem(3, "Samsung Galaxy S24", 1, 849.99));
        order4.addItem(new OrderItem(25, "Air Fryer", 1, 89.99));
        order4.addItem(new OrderItem(26, "Blender", 1, 129.99));
        orders.add(order4);

        Order order5 = new Order(orderIdCounter++, "David Wilson", "david.wilson@email.com", "PENDING");
        order5.addItem(new OrderItem(8, "PlayStation 5", 1, 499.99));
        order5.addItem(new OrderItem(9, "Nintendo Switch OLED", 1, 349.99));
        orders.add(order5);

        Order order6 = new Order(orderIdCounter++, "Jennifer Lee", "jennifer.lee@email.com", "CANCELLED");
        order6.addItem(new OrderItem(6, "Apple Watch Series 9", 1, 429.99));
        order6.addItem(new OrderItem(30, "Running Shoes", 1, 89.99));
        orders.add(order6);

        Order order7 = new Order(orderIdCounter++, "Robert Taylor", "robert.taylor@email.com", "SHIPPED");
        order7.addItem(new OrderItem(4, "Sony WH-1000XM5", 1, 349.99));
        order7.addItem(new OrderItem(10, "Bose SoundLink", 1, 129.99));
        order7.addItem(new OrderItem(31, "Winter Jacket", 1, 149.99));
        orders.add(order7);

        System.out.println("✅ Mock data initialized: " + products.size() + " products, " + orders.size() + " orders");
    }

    // Product CRUD operations
    public List<Product> getAllProducts() {
        return new ArrayList<>(products);
    }

    public Product getProductById(int id) {
        return products.stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public List<Product> getProductsByCategory(String category) {
        if ("All Categories".equals(category) || category == null) {
            return getAllProducts();
        }
        return products.stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllProducts();
        }

        String searchTerm = query.toLowerCase().trim();
        return products.stream()
                .filter(p -> p.getName().toLowerCase().contains(searchTerm) ||
                        p.getDescription().toLowerCase().contains(searchTerm) ||
                        p.getCategory().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }

    public boolean addProduct(Product product) {
        product.setId(productIdCounter++);
        boolean added = products.add(product);
        if (added) {
            System.out.println("✅ Added new product: " + product.getName() + " (ID: " + product.getId() + ")");
        } else {
            System.out.println("❌ Failed to add product: " + product.getName());
        }
        return added;
    }

    public boolean updateProduct(Product updatedProduct) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == updatedProduct.getId()) {
                Product oldProduct = products.get(i);
                products.set(i, updatedProduct);
                System.out.println("✅ Updated product: " + updatedProduct.getName() + " (ID: " + updatedProduct.getId() + ")");
                System.out.println("   Old price: $" + oldProduct.getPrice() + " → New price: $" + updatedProduct.getPrice());
                System.out.println("   Old stock: " + oldProduct.getStockQuantity() + " → New stock: " + updatedProduct.getStockQuantity());
                return true;
            }
        }
        System.out.println("❌ Product not found for update: ID " + updatedProduct.getId());
        return false;
    }

    public boolean deleteProduct(int id) {
        boolean removed = products.removeIf(product -> product.getId() == id);
        if (removed) {
            System.out.println("✅ Product ID " + id + " permanently deleted from inventory");
        } else {
            System.out.println("❌ Product ID " + id + " not found for deletion");
        }
        return removed;
    }

    // Order operations
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }

    public List<Order> getOrdersByStatus(String status) {
        if ("ALL".equals(status)) {
            return getAllOrders();
        }
        return orders.stream()
                .filter(o -> o.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    public Order getOrderById(int id) {
        return orders.stream()
                .filter(o -> o.getId() == id)
                .findFirst()
                .orElse(null);
    }

    public boolean updateOrderStatus(int orderId, String newStatus) {
        Order order = getOrderById(orderId);
        if (order != null) {
            String oldStatus = order.getStatus();
            order.setStatus(newStatus);
            System.out.println("✅ Order #" + orderId + " status changed: " + oldStatus + " → " + newStatus);
            System.out.println("   Customer: " + order.getCustomerName());
            System.out.println("   Total: $" + order.getTotalAmount());
            return true;
        }
        System.out.println("❌ Order not found: #" + orderId);
        return false;
    }

    // Analytics and Statistics
    public int getTotalProducts() {
        return products.size();
    }

    public int getTotalOrders() {
        return orders.size();
    }

    public int getPendingOrders() {
        return (int) orders.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
    }

    public int getConfirmedOrders() {
        return (int) orders.stream().filter(o -> "CONFIRMED".equals(o.getStatus())).count();
    }

    public int getShippedOrders() {
        return (int) orders.stream().filter(o -> "SHIPPED".equals(o.getStatus())).count();
    }

    public int getDeliveredOrders() {
        return (int) orders.stream().filter(o -> "DELIVERED".equals(o.getStatus())).count();
    }

    public int getCancelledOrders() {
        return (int) orders.stream().filter(o -> "CANCELLED".equals(o.getStatus())).count();
    }

    public double getTotalRevenue() {
        return orders.stream()
                .filter(o -> "DELIVERED".equals(o.getStatus()))
                .mapToDouble(Order::getTotalAmount)
                .sum();
    }

    public List<String> getAllCategories() {
        return products.stream()
                .map(Product::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    public int getLowStockProducts(int threshold) {
        return (int) products.stream()
                .filter(p -> p.getStockQuantity() <= threshold)
                .count();
    }

    // Get popular products (for analytics)
    public List<Product> getPopularProducts(int limit) {
        return products.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getStockQuantity(), p1.getStockQuantity())) // Sort by stock (reverse)
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Get recent orders
    public List<Order> getRecentOrders(int limit) {
        return orders.stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate())) // Sort by date (newest first)
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Add sample order for testing
    public void addSampleOrder() {
        Order newOrder = new Order(orderIdCounter++,
                "Test Customer",
                "test@email.com",
                "PENDING");

        // Add random products to the order
        if (!products.isEmpty()) {
            Product product1 = products.get(0);
            Product product2 = products.size() > 1 ? products.get(1) : products.get(0);

            newOrder.addItem(new OrderItem(product1.getId(), product1.getName(), 1, product1.getPrice()));
            newOrder.addItem(new OrderItem(product2.getId(), product2.getName(), 2, product2.getPrice()));

            orders.add(newOrder);
            System.out.println("✅ Added sample order #" + newOrder.getId() + " with total: $" + newOrder.getTotalAmount());
        }
    }

    // Clear all data (for testing)
    public void clearAllData() {
        products.clear();
        orders.clear();
        productIdCounter = 1;
        orderIdCounter = 1;
        System.out.println("🗑️ All mock data cleared");
    }

    // Reinitialize data
    public void reinitializeData() {
        clearAllData();
        initializeMockData();
        System.out.println("🔄 Mock data reinitialized");
    }
}