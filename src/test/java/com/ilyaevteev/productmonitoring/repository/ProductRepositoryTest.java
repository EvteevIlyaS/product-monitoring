package com.ilyaevteev.productmonitoring.repository;

import com.ilyaevteev.productmonitoring.model.Category;
import com.ilyaevteev.productmonitoring.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void getProductsByCategoryName_checkReturnedValue() {
        List<Product> products = productRepository.getProductsByCategoryName("fruits");

        assertThat(products.size()).isEqualTo(2);

    }

    @Test
    void updateProduct_checkReturnedValue() {
        Long id = 1L;
        String name = "new name";
        Category category = categoryRepository.findById(1L).get();

        productRepository.updateProductNameAndCategory(name, category, id);
        Product product = productRepository.findById(id).get();

        assertThat(product.getCategory()).isEqualTo(category);
        assertThat(product.getName()).isEqualTo(name);
    }
}