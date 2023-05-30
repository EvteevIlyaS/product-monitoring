package com.ilyaevteev.productmonitoring.rest.user;

import com.ilyaevteev.productmonitoring.dto.response.ProductInfoDto;
import com.ilyaevteev.productmonitoring.service.*;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public Page<ProductInfoDto> getProductsByCategory(@RequestParam String category, @PageableDefault(size = 20) Pageable pageable) {
        return productService.getProductsByCategory(category, pageable)
                .map(el -> entityDtoMapper.toDto(el, ProductInfoDto.class));
    }
}
