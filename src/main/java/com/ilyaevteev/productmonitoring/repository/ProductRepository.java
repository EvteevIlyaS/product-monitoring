package com.ilyaevteev.productmonitoring.repository;

import com.ilyaevteev.productmonitoring.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> getProductsByCategoryName(String name);
}
