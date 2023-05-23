package com.ilyaevteev.productmonitoring.rest.user;

import com.ilyaevteev.productmonitoring.dto.response.CategoryDto;
import com.ilyaevteev.productmonitoring.service.*;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController(value = "categoryRestControllerUser")
@RequestMapping(value = "/api/v1/user/categories")
@RequiredArgsConstructor
public class CategoryRestController {
    private final CategoryService categoryService;

    private final EntityDtoMapper entityDtoMapper;

    @GetMapping
    @Operation(summary = "Показать справочник категорий товаров")
    public ResponseEntity<List<CategoryDto>> getCategoriesDirectory(@RequestParam int offset,
                                                                    @RequestParam(name = "page-size") int pageSize) {
        List<CategoryDto> categoriesDirectory = categoryService.getCategoriesDirectory(offset, pageSize).stream()
                .map(el -> entityDtoMapper.toDto(el, CategoryDto.class)).toList();

        return new ResponseEntity<>(categoriesDirectory, HttpStatus.OK);
    }
}