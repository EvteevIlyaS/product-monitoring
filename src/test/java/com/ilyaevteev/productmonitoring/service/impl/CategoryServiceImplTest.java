package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.model.Category;
import com.ilyaevteev.productmonitoring.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void getCategoriesDirectory_checkReturnedValue() {
        List<Category> categories = Arrays.asList(new Category(), new Category());
        int offset = 0;
        int pageSize = 2;
        Page<Category> page = new PageImpl<>(categories);
        when(categoryRepository.findAll((Pageable) any())).thenReturn(page);

        List<Category> categoriesRes = categoryService.getCategoriesDirectory(offset, pageSize);

        assertThat(categoriesRes).isEqualTo(page.getContent());
    }

    @Test
    void getCategoryById_checkReturnedValue() {
        Long id = 1L;
        Category fruits = new Category();
        Optional<Category> optionalCategory = Optional.of(fruits);
        when(categoryRepository.findById(id)).thenReturn(optionalCategory);

        Category category = categoryService.getCategoryById(id);

        assertThat(category).isEqualTo(fruits);
    }

    @Test
    void getCategoryById_checkThrowException() {
        Long id = 1L;
        Optional<Category> optionalCategory = Optional.empty();
        when(categoryRepository.findById(id)).thenReturn(optionalCategory);

        assertThatThrownBy(() -> categoryService.getCategoryById(id))
                .hasMessage("No categories found by id: " + id);
    }
}