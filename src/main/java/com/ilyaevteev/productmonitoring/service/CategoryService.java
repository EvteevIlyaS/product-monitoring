package com.ilyaevteev.productmonitoring.service;

import com.ilyaevteev.productmonitoring.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getCategoriesDirectory(int offset, int pageSize);

    Category getCategoryById(Long id);
}
