package com.ilyaevteev.productmonitoring.service;


import com.ilyaevteev.productmonitoring.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductService {
    List<Product> getProductsByCategory(String name);

    Map<String, String> addProduct(Product product);

    Map<String, String> updateProduct(Product product);

    Map<String, String> deleteProduct(Long id);

    Product getProductById(Long id);

    Map<String, String> uploadFileProduct(MultipartFile file);

}
