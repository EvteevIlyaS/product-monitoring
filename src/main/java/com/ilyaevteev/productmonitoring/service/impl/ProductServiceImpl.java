package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.exception.exceptionlist.BadRequestException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.FailedDependencyException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.NotFoundException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.UnsupportedMediaTypeException;
import com.ilyaevteev.productmonitoring.util.CSVHelper;
import com.ilyaevteev.productmonitoring.model.Product;
import com.ilyaevteev.productmonitoring.repository.ProductRepository;
import com.ilyaevteev.productmonitoring.service.CategoryService;
import com.ilyaevteev.productmonitoring.service.ProductService;
import com.ilyaevteev.productmonitoring.util.ExcelHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    private final CategoryService categoryService;

    @Override
    public Page<Product> getProductsByCategory(String name, Pageable pageable) {
        Page<Product> products = productRepository.getProductsByCategoryName(name, pageable);
        if (products.getContent().size() == 0) {
            String message = "No products found";
            log.error(message);
            throw new NotFoundException(message);
        }
        return products;
    }

    @Override
    public Map<String, String> addProduct(Product product) {
        try {
            Product savedProduct = productRepository.save(product);
            return Map.of("id", savedProduct.getId().toString(),
                    "name", savedProduct.getName());
        } catch (Exception e) {
            String message = "Wrong product data";
            log.error(message);
            throw new BadRequestException(message);
        }
    }

    @Override
    @Transactional
    public Map<String, String> updateProduct(Product product) {
        try {
            Long id = product.getId();
            String name = product.getName();
            if (id == null) {
                String message = "No id provided to update product";
                log.error(message);
                throw new BadRequestException(message);
            }
            getProductById(id);
            productRepository.updateProductNameAndCategory(name, product.getCategory(), id);
            return Map.of("id", id.toString(),
                    "name", name);
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
    @Transactional
    public Map<String, String> deleteProduct(Long id) {
        int rowsNumber = productRepository.deleteById(id.toString());
        if (rowsNumber == 0) {
            String message = "No products found";
            log.error(message);
            throw new NotFoundException(message);
        }
        return Map.of("id", id.toString());
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseGet(() -> {
            String message = "No products found";
            log.error(message);
            throw new NotFoundException(message);
        });
    }

    @Override
    @Transactional
    public Map<String, String> uploadFileProduct(MultipartFile file) {
        Boolean isCSV = CSVHelper.hasCSVFormat(file);
        Boolean isExcel = ExcelHelper.hasExcelFormat(file);

        if (isCSV | isExcel) {
            List<Product> products = new ArrayList<>();
            try {
                List<Map<String, String>> rawProductsData = isCSV ?
                        CSVHelper.csvToEntities(file.getInputStream(), CSVHelper.PRODUCT_HEADERS) :
                        ExcelHelper.excelToEntities(file.getInputStream(), ExcelHelper.PRODUCT_HEADERS);
                rawProductsData.forEach(p -> {
                    Product product = new Product();
                    product.setName(p.get("name"));
                    product.setCategory(categoryService.getCategoryById(Long.parseLong(p.get("categoryId"))));
                    products.add(product);
                });

                productRepository.saveAll(products);
                return Map.of("file", Objects.requireNonNull(file.getOriginalFilename()));

            } catch (Exception e) {
                String message = "Fail to store data";
                log.error(message);
                if (e.getMessage() != null && e.getMessage().equals("No categories found")) {
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
