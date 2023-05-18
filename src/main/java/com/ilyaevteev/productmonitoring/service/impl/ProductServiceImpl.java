package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.exception.exceptionlist.BadRequestException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.FailedDependencyException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.UnsupportedMediaTypeException;
import com.ilyaevteev.productmonitoring.util.CSVHelper;
import com.ilyaevteev.productmonitoring.model.Product;
import com.ilyaevteev.productmonitoring.repository.ProductRepository;
import com.ilyaevteev.productmonitoring.service.CategoryService;
import com.ilyaevteev.productmonitoring.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    @Override
    public List<Product> getProductsByCategory(String name) {
        return productRepository.getProductsByCategoryName(name);
    }

    @Override
    public void addProduct(Product product) {
        try {
            productRepository.save(product);
        } catch (Exception e) {
            String message = "Wrong product data";
            log.error(message);
            throw new BadRequestException(message);
        }
    }

    @Override
    @Transactional
    public void updateProduct(Product product) {
        Long id = product.getId();
        try {
            if (id == null) {
                String message = "No id provided to update product";
                log.error(message);
                throw new BadRequestException(message);
            }
            getProductById(id);
            productRepository.updateProductNameAndCategory(product.getName(), product.getCategory(), id);
        } catch (Exception e) {
            String message = "Wrong product data";
            log.error(message);
            if (e.getMessage().contains("No products found by id")) {
                throw new FailedDependencyException(message);
            }
            throw new BadRequestException(message);
        }
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseGet(() -> {
            String message = "No products found by id: " + id;
            log.error(message);
            throw new BadRequestException(message);
        });
    }

    @Override
    @Transactional
    public void uploadFileProduct(MultipartFile file) {
        List<Product> products;

        if (CSVHelper.hasCSVFormat(file)) {
            products = new ArrayList<>();
            try {
                List<Map<String, String>> rawProductsData = CSVHelper.csvToEntities(file.getInputStream(), CSVHelper.PRODUCT_HEADERS);
                rawProductsData.forEach(p -> {
                    Product product = new Product();
                    product.setName(p.get("name"));
                    product.setCategory(categoryService.getCategoryById(Long.parseLong(p.get("categoryId"))));
                    products.add(product);
                });

                productRepository.saveAll(products);

            } catch (Exception e) {
                String message = "Fail to store csv data";
                log.error(message);
                if (e.getMessage().contains("No categories found by id")) {
                    throw new FailedDependencyException(message);
                }
                throw new BadRequestException(message);
            }

        } else {
            String message = "Wrong data format";
            log.error(message);
            throw new UnsupportedMediaTypeException(message);
        }
    }
}
