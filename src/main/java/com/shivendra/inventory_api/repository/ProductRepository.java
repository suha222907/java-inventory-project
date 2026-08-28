package com.shivendra.inventory_api.repository;

import com.shivendra.inventory_api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    List<Product> findByQuantityLessThan(Integer threshold);
}

