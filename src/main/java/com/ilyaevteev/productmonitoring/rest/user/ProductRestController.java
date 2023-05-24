package com.ilyaevteev.productmonitoring.rest.user;

import com.ilyaevteev.productmonitoring.dto.response.ProductInfoDto;
import com.ilyaevteev.productmonitoring.service.*;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "productRestControllerUser")
@RequestMapping(value = "/api/v1/user/products")
@RequiredArgsConstructor
public class ProductRestController {
    private final ProductService productService;

    private final EntityDtoMapper entityDtoMapper;

    @GetMapping
    @Operation(summary = "Просмотреть список товаров по категориям")
    public ResponseEntity<Page<ProductInfoDto>> getProductsByCategory(@RequestParam String category, Pageable pageable) {
        Page<ProductInfoDto> products = productService.getProductsByCategory(category, pageable)
                .map(el -> entityDtoMapper.toDto(el, ProductInfoDto.class));

        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
