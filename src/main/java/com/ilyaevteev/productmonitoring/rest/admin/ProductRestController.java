package com.ilyaevteev.productmonitoring.rest.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyaevteev.productmonitoring.dto.request.ProductRequestDto;
import com.ilyaevteev.productmonitoring.dto.response.ProductDeletionDto;
import com.ilyaevteev.productmonitoring.dto.response.ProductFormattingDto;
import com.ilyaevteev.productmonitoring.dto.response.ProductResponseDto;
import com.ilyaevteev.productmonitoring.dto.response.UploadDataDto;
import com.ilyaevteev.productmonitoring.model.Product;
import com.ilyaevteev.productmonitoring.service.ProductService;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "Добавить товар")
    public ResponseEntity<ProductFormattingDto> createProduct(@RequestBody ProductRequestDto productRequestDto) {
        ProductFormattingDto product = objectMapper.convertValue(productService.addProduct(entityDtoMapper.toEntity(productRequestDto, Product.class)),
                ProductFormattingDto.class);

        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @PutMapping
    @Operation(summary = "Редактировать товар")
    public ResponseEntity<ProductFormattingDto> updateProduct(@RequestBody ProductResponseDto productResponseDto) {
        ProductFormattingDto product = objectMapper.convertValue(productService.updateProduct(entityDtoMapper.toEntity(productResponseDto, Product.class)),
                ProductFormattingDto.class);

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Удалить товар")
    public ResponseEntity<ProductDeletionDto> deleteProduct(@PathVariable Long id) {
        ProductDeletionDto product = objectMapper.convertValue(productService.deleteProduct(id),
                ProductDeletionDto.class);

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping(value = "/upload")
    @Operation(summary = "Выгрузить информации о продуктах в формате csv/xlsx")
    public ResponseEntity<UploadDataDto> uploadProducts(@RequestBody MultipartFile file) {
        UploadDataDto uploadData = objectMapper.convertValue(productService.uploadFileProduct(file), UploadDataDto.class);

        return new ResponseEntity<>(uploadData, HttpStatus.CREATED);
    }
}
