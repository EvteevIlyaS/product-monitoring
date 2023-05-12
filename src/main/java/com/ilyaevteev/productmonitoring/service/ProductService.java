package com.ilyaevteev.productmonitoring.service;


import com.ilyaevteev.productmonitoring.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List<Product> getProductsByCategory(String name);

    void addOrUpdateProduct(Product product);

    void deleteProduct(Long id);

    Product getProductById(Long id);

    void uploadFileProduct(MultipartFile file);

}
