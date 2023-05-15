package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.model.Category;
import com.ilyaevteev.productmonitoring.repository.CategoryRepository;
import com.ilyaevteev.productmonitoring.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getCategoriesDirectory(int offset, int pageSize) {
        Pageable page = PageRequest.of(offset, pageSize);
        return categoryRepository.findAll(page).getContent();
    }

    @Override
    public Category getCategoryById(Long id) {
        Optional<Category> category = categoryRepository.findById(id);

        if (category.isEmpty()) {
            String message = "No categories found by id: " + id;
            log.error(message);
            throw new RuntimeException(message);
        }

        return category.get();
    }
}
