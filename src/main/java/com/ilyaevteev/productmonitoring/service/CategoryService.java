package com.ilyaevteev.productmonitoring.service;

import com.ilyaevteev.productmonitoring.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {
    Page<Category> getCategoriesDirectory(Pageable pageable);

    Category getCategoryById(Long id);
}
