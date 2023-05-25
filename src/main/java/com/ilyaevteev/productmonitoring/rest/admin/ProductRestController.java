package com.ilyaevteev.productmonitoring.rest.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyaevteev.productmonitoring.dto.request.EditionProductDto;
import com.ilyaevteev.productmonitoring.dto.request.ProductRequestDto;
import com.ilyaevteev.productmonitoring.dto.response.ProductDeletionDto;
import com.ilyaevteev.productmonitoring.dto.response.ProductFormattingDto;
import com.ilyaevteev.productmonitoring.dto.response.UploadDataDto;
import com.ilyaevteev.productmonitoring.model.Product;
import com.ilyaevteev.productmonitoring.service.ProductService;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController(value = "productRestControllerAdmin")
@RequestMapping(value = "/api/v1/admin/products")
@RequiredArgsConstructor
public class ProductRestController {
    private final ProductService productService;

    private final EntityDtoMapper entityDtoMapper;
    private final ObjectMapper objectMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить товар")
    public ProductFormattingDto createProduct(@RequestBody ProductRequestDto productRequestDto) {
        return objectMapper.convertValue(productService.addProduct(entityDtoMapper.toEntity(productRequestDto, Product.class)),
                ProductFormattingDto.class);
    }

    @PutMapping
    @Operation(summary = "Редактировать товар")
    public ProductFormattingDto updateProduct(@RequestBody EditionProductDto editionProductDto) {
        return objectMapper.convertValue(productService.updateProduct(entityDtoMapper.toEntity(editionProductDto, Product.class)),
                ProductFormattingDto.class);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Удалить товар")
    public ProductDeletionDto deleteProduct(@PathVariable Long id) {
        return objectMapper.convertValue(productService.deleteProduct(id),
                ProductDeletionDto.class);
    }

    @PostMapping(value = "/upload")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Выгрузить информации о продуктах в формате csv/xlsx")
    public UploadDataDto uploadProducts(@RequestBody MultipartFile file) {
        return objectMapper.convertValue(productService.uploadFileProduct(file), UploadDataDto.class);
    }
}
