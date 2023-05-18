package com.ilyaevteev.productmonitoring.rest;

import com.ilyaevteev.productmonitoring.dto.NoIdProductDto;
import com.ilyaevteev.productmonitoring.dto.ProductDto;
import com.ilyaevteev.productmonitoring.dto.StoreProductPriceDto;
import com.ilyaevteev.productmonitoring.dto.auth.RegistrationDto;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/admin/")
@RequiredArgsConstructor
public class AdminRestController {
    private final ProductService productService;
    private final StoreProductPriceService storeProductPriceService;
    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder;
    private final EntityDtoMapper entityDtoMapper;

    @PostMapping("add-admin")
    @Operation(summary = "Выполнить процедуру регистрации администратора")
    public ResponseEntity<Map<String, String>> register(@RequestBody @Valid RegistrationDto requestDto, BindingResult bindingResult) {
        User user = userService.register(entityDtoMapper.toEntity(requestDto, User.class), passwordEncoder, "ROLE_ADMIN", bindingResult);

        Map<String, String> response = new HashMap<>();
        response.put("username", user.getUsername());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "products")
    @Operation(summary = "Добавить товар")
    public ResponseEntity<Map<String, String>> createProduct(@RequestBody NoIdProductDto noIdProductDto) {
        productService.addProduct(entityDtoMapper.toEntity(noIdProductDto, Product.class));

        Map<String, String> response = new HashMap<>();
        response.put("product name", noIdProductDto.getName());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping(value = "products")
    @Operation(summary = "Редактировать товар")
    public ResponseEntity<Map<String, String>> updateProduct(@RequestBody ProductDto productDto) {
        productService.updateProduct(entityDtoMapper.toEntity(productDto, Product.class));

        Map<String, String> response = new HashMap<>();
        response.put("product name", productDto.getName());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "products/{id}")
    @Operation(summary = "Удалить товар")
    public ResponseEntity<Map<String, Long>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);

        Map<String, Long> response = new HashMap<>();
        response.put("deleted product id", id);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "store-product-prices")
    @Operation(summary = "Привязать цену к товару в конкретном магазине на текущий момент")
    public ResponseEntity<Map<String, Long>> createStoreProductPrice(@RequestBody StoreProductPriceDto storeProductPriceDto) {
        storeProductPriceDto.setDate(new Date());
        storeProductPriceService.addStoreProductPrice(entityDtoMapper.toEntity(storeProductPriceDto, StoreProductPrice.class));

        Map<String, Long> response = new HashMap<>();
        response.put("store id", storeProductPriceDto.getStore().getId());
        response.put("product id", storeProductPriceDto.getProduct().getId());
        response.put("price", storeProductPriceDto.getPrice());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "products/upload")
    @Operation(summary = "Выгрузить информации о продуктах в формате csv")
    public ResponseEntity<Map<String, String>> uploadProducts(@RequestBody MultipartFile file) {
        productService.uploadFileProduct(file);

        Map<String, String> response = new HashMap<>();
        response.put("file name", file.getOriginalFilename());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping(value = "store-product-prices/upload")
    @Operation(summary = "Выгрузить информации о ценах в формате csv")
    public ResponseEntity<Map<String, String>> uploadPrices(@RequestBody MultipartFile file) {
        storeProductPriceService.uploadFilePrices(file);

        Map<String, String> response = new HashMap<>();
        response.put("file name", file.getOriginalFilename());

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
