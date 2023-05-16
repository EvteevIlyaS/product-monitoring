package com.ilyaevteev.productmonitoring.repository;

import com.ilyaevteev.productmonitoring.model.Category;
import com.ilyaevteev.productmonitoring.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> getProductsByCategoryName(String name);

    @Modifying
    @Query("update Product product set product.name = :name, product.category = :category where product.id = :id")
    void updateProductNameAndCategory(@Param("name") String name, @Param("category") Category category, @Param("id") Long id);
}
