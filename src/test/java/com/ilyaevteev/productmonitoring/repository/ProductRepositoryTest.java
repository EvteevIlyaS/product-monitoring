package com.ilyaevteev.productmonitoring.repository;

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

    @Test
    void getProductsByCategoryName_checkReturnedValue() {
        List<Product> products = productRepository.getProductsByCategoryName("fruits");

        assertThat(products.size()).isEqualTo(2);

    }
}