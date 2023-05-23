package com.ilyaevteev.productmonitoring.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyaevteev.productmonitoring.dto.request.ProductRequestDto;
import com.ilyaevteev.productmonitoring.dto.request.StoreProductPriceRequestDto;
import com.ilyaevteev.productmonitoring.dto.request.RegistrationRequestDto;
import com.ilyaevteev.productmonitoring.dto.response.*;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import com.ilyaevteev.productmonitoring.model.Product;
import com.ilyaevteev.productmonitoring.model.StoreProductPrice;
import com.ilyaevteev.productmonitoring.model.auth.User;
import com.ilyaevteev.productmonitoring.service.ProductService;
import com.ilyaevteev.productmonitoring.service.StoreProductPriceService;
import com.ilyaevteev.productmonitoring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/v1/admin/")
@RequiredArgsConstructor
public class AdminRestController {
    private final ProductService productService;
    private final StoreProductPriceService storeProductPriceService;
    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder;
    private final EntityDtoMapper entityDtoMapper;
    private final ObjectMapper objectMapper;

    @PostMapping("add-admin")
    @Operation(summary = "Выполнить процедуру регистрации администратора")
    public ResponseEntity<RegistrationResponseDto> register(@RequestBody @Valid RegistrationRequestDto requestDto, BindingResult bindingResult) {
        RegistrationResponseDto user = objectMapper.convertValue(userService.register(entityDtoMapper.toEntity(requestDto, User.class),
                passwordEncoder, "ROLE_ADMIN", bindingResult), RegistrationResponseDto.class);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping(value = "products")
    @Operation(summary = "Добавить товар")
    public ResponseEntity<ProductFormattingDto> createProduct(@RequestBody ProductRequestDto productRequestDto) {
        ProductFormattingDto product = objectMapper.convertValue(productService.addProduct(entityDtoMapper.toEntity(productRequestDto, Product.class)),
                ProductFormattingDto.class);

        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    @PutMapping(value = "products")
    @Operation(summary = "Редактировать товар")
    public ResponseEntity<ProductFormattingDto> updateProduct(@RequestBody ProductResponseDto productResponseDto) {
        ProductFormattingDto product = objectMapper.convertValue(productService.updateProduct(entityDtoMapper.toEntity(productResponseDto, Product.class)),
                ProductFormattingDto.class);

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @DeleteMapping(value = "products/{id}")
    @Operation(summary = "Удалить товар")
    public ResponseEntity<ProductDeletionDto> deleteProduct(@PathVariable Long id) {
        ProductDeletionDto product = objectMapper.convertValue(productService.deleteProduct(id),
                ProductDeletionDto.class);

        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PostMapping(value = "store-product-prices")
    @Operation(summary = "Привязать цену к товару в конкретном магазине на текущий момент")
    public ResponseEntity<StoreProductPriceResponseDto> createStoreProductPrice(@RequestBody StoreProductPriceRequestDto storeProductPriceRequestDto) {
        StoreProductPriceResponseDto storeProductPrice = objectMapper.convertValue(storeProductPriceService.addStoreProductPrice(
                entityDtoMapper.toEntity(storeProductPriceRequestDto, StoreProductPrice.class)), StoreProductPriceResponseDto.class);

        return new ResponseEntity<>(storeProductPrice, HttpStatus.CREATED);
    }

    @DeleteMapping (value = "store-product-prices/{id}")
    @Operation(summary = "Удалить цену к товару в конкретном магазине")
    public ResponseEntity<DeletionStoreProductPriceResponseDto> deleteStoreProductPrice(@PathVariable Long id) {
        DeletionStoreProductPriceResponseDto storeProductPrice = objectMapper.convertValue(storeProductPriceService.deleteProductPriceStore(id),
                DeletionStoreProductPriceResponseDto.class);

        return new ResponseEntity<>(storeProductPrice, HttpStatus.OK);
    }

    @PostMapping(value = "products/upload")
    @Operation(summary = "Выгрузить информации о продуктах в формате csv/xlsx")
    public ResponseEntity<UploadDataDto> uploadProducts(@RequestBody MultipartFile file) {
        UploadDataDto uploadData = objectMapper.convertValue(productService.uploadFileProduct(file), UploadDataDto.class);

        return new ResponseEntity<>(uploadData, HttpStatus.CREATED);
    }

    @PostMapping(value = "store-product-prices/upload")
    @Operation(summary = "Выгрузить информации о ценах в формате csv/xlsx")
    public ResponseEntity<UploadDataDto> uploadPrices(@RequestBody MultipartFile file) {
        UploadDataDto uploadData = objectMapper.convertValue(storeProductPriceService.uploadFilePrices(file), UploadDataDto.class);

        return new ResponseEntity<>(uploadData, HttpStatus.CREATED);
    }
}
