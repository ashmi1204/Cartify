package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.GeneratedKeyHolder;

// ✅ Java SQL Imports (Fixes setString, setDouble, Statement)
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.List;

@Repository
public class ProductRepo {
    private JdbcTemplate jdbcTemplate;
    private final RowMapper<Product> productRowMapper = new RowMapper<Product>() {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setId(rs.getLong("product_id"));
            product.setName(rs.getString("product_name"));
            product.setDescription(rs.getString("product_description"));
            product.setPrice(rs.getDouble("product_price"));
            product.setQuantity(rs.getInt("product_qty"));
            product.setCategory(rs.getString("category"));
            product.setImage(rs.getString("image"));
            return product;
        }
    };

    public ProductRepo(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    //CRUD

    public Product save(Product product){
        String sql = "INSERT INTO products(product_name,product_description,product_price,product_qty,category,image) VALUES(?,?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            // 2. Prepare statement, telling MySQL to return the generated keys
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, product.getName());
            ps.setString(2,product.getDescription());
            ps.setDouble(3, product.getPrice());
            ps.setInt(4,product.getQuantity());
            ps.setString(5,product.getCategory());
            ps.setString(6,product.getImage());
            return ps;
        }, keyHolder);

        // 3. Set the generated ID back onto the Product object
        if (keyHolder.getKey() != null) {
            product.setId(keyHolder.getKey().longValue());
        }

        // 4. Return the complete Product object (satisfies the ProductService return type)
        return product;
    }

    public Optional<Product> findById(Long id){
        String sql = "SELECT product_id, product_name, product_description, product_price, product_qty, category, image FROM Products WHERE product_id = ?";
        try{
            Product product = jdbcTemplate.queryForObject(sql,productRowMapper,id);
            return Optional.ofNullable(product);
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
    public Product update(Product product) {
        String sql = "UPDATE Products SET product_name = ?, product_description = ?, product_price = ?, product_qty = ?, category = ?, image = ? WHERE product_id = ?";

        int rowsAffected =  jdbcTemplate.update(
                sql,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory(),
                product.getImage(),
                product.getId()
        );
        if (rowsAffected == 0) {
            // If no rows were updated, the product likely didn't exist.
            throw new RuntimeException("Update failed. Product with ID " + product.getId() + " not found.");
        }

        return product;
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM Products WHERE product_id = ?";

        return jdbcTemplate.update(sql, id);
    }

    public List<Product> findAll(){
        String sql = "SELECT product_id,product_name,product_description,product_price,product_qty,category,image FROM products";
        return jdbcTemplate.query(sql,productRowMapper);
    }
}
