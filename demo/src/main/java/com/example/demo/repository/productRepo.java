package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class productRepo {
    private JdbcTemplate jdbcTemplate;
    private final RowMapper<Product> productRowMapper = new RowMapper<Product>() {
        @Override
        public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
            Product product = new Product();
            product.setId(rs.getLong("product_id"));
            product.setName(rs.getString("product_name"));
            product.setDescription(rs.getString("product_description"));
            product.setCategory(rs.getString("category"));
            product.setImage(rs.getString("image"));
            product.setPrice(rs.getDouble("product_price"));
            product.setQuantity(rs.getInt("product_qty"));
            return product;
        }
    };

    public productRepo(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    //CRUD

    public int save(Product product){
        String sql = "INSERT INTO products(product_name,product_description,product_price,product_qty,category,image) VALUES(?,?,?,?,?,?)";
        return jdbcTemplate.update(sql,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory(),
                product.getImage()
                );
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
    public int update(Product product) {
        String sql = "UPDATE Products SET product_name = ?, product_description = ?, product_price = ?, product_qty = ?, category = ?, image = ? WHERE product_id = ?";

        return jdbcTemplate.update(
                sql,
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory(),
                product.getImage(),
                product.getId()
        );
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM Products WHERE product_id = ?";

        return jdbcTemplate.update(sql, id);
    }
}
