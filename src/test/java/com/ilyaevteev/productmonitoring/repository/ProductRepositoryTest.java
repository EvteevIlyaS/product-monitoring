package com.ilyaevteev.productmonitoring.repository;

import com.ilyaevteev.productmonitoring.model.Category;
import com.ilyaevteev.productmonitoring.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void getProductsByCategoryName_checkReturnedValue() {
        Pageable pageable = PageRequest.of(0, 2);
        Page<Product> products = productRepository.getProductsByCategoryName("fruits", pageable);

        assertThat(products.getContent().size()).isEqualTo(2);

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