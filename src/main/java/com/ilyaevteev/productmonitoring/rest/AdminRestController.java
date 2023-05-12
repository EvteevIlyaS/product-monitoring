package com.ilyaevteev.productmonitoring.rest;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/admin/")
public class AdminRestController {
    private final ProductService productService;
    private final StoreProductPriceService storeProductPriceService;
    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder;
    private final EntityDtoMapper entityDtoMapper;

    @Autowired
    public AdminRestController(ProductService productService, StoreProductPriceService storeProductPriceService, UserService userService, BCryptPasswordEncoder passwordEncoder, EntityDtoMapper entityDtoMapper) {
        this.productService = productService;
        this.storeProductPriceService = storeProductPriceService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.entityDtoMapper = entityDtoMapper;
    }

    @PostMapping("register")
    @Operation(summary = "Выполнить процедуру регистрации администратора")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegistrationDto requestDto) {
        User user = userService.register(entityDtoMapper.toEntity(requestDto, User.class), passwordEncoder, "ROLE_ADMIN");

        Map<String, String> response = new HashMap<>();
        response.put("username", user.getUsername());

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "products")
    @Operation(summary = "Добавить/редактировать товар")
    public ResponseEntity<Map<String, String>> createUpdateProduct(@RequestBody ProductDto productDto) {
        productService.addOrUpdateProduct(entityDtoMapper.toEntity(productDto, Product.class));

        Map<String, String> response = new HashMap<>();
        response.put("product name", productDto.getName());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "products/{id}")
    @Operation(summary = "Удалить товар")
    public ResponseEntity<Map<String, Long>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);

        Map<String, Long> response = new HashMap<>();
        response.put("deleted product id", id);

        return ResponseEntity.ok(response);
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

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "products/upload")
    @Operation(summary = "Выгрузить информации о продуктах в формате csv")
    public ResponseEntity<Map<String, String>> uploadProducts(@RequestBody MultipartFile file) {
        productService.uploadFileProduct(file);

        Map<String, String> response = new HashMap<>();
        response.put("file name", file.getOriginalFilename());

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "store-product-prices/upload")
    @Operation(summary = "Выгрузить информации о ценах в формате csv")
    public ResponseEntity<Map<String, String>> uploadPrices(@RequestBody MultipartFile file) {
        storeProductPriceService.uploadFilePrices(file);

        Map<String, String> response = new HashMap<>();
        response.put("file name", file.getOriginalFilename());

        return ResponseEntity.ok(response);
    }
}
