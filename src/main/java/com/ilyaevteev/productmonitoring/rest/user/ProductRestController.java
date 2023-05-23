package com.ilyaevteev.productmonitoring.rest.user;

import com.ilyaevteev.productmonitoring.dto.response.ProductResponseDto;
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

@RestController(value = "productRestControllerUser")
@RequestMapping(value = "/api/v1/user/products")
@RequiredArgsConstructor
public class ProductRestController {
    private final ProductService productService;

    private final EntityDtoMapper entityDtoMapper;

    @GetMapping
    @Operation(summary = "Просмотреть список товаров по категориям")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(@RequestParam String category) {
        List<ProductResponseDto> products = productService.getProductsByCategory(category).stream()
                .map(el -> entityDtoMapper.toDto(el, ProductResponseDto.class)).toList();

        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
