package com.ilyaevteev.productmonitoring.rest;

import com.ilyaevteev.productmonitoring.dto.CategoryDto;
import com.ilyaevteev.productmonitoring.dto.ProductDto;
import com.ilyaevteev.productmonitoring.dto.StoreDto;
import com.ilyaevteev.productmonitoring.dto.StoreProductPriceDto;
import com.ilyaevteev.productmonitoring.dto.auth.NewEmailDto;
import com.ilyaevteev.productmonitoring.dto.auth.NewPasswordDto;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import com.ilyaevteev.productmonitoring.service.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/user/")
public class UserRestController {
    private final ProductService productService;
    private final StoreService storeService;
    private final CategoryService categoryService;
    private final StoreProductPriceService storeProductPriceService;
    private final UserService userService;

    private final EntityDtoMapper entityDtoMapper;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserRestController(ProductService productService, StoreService storeService, CategoryService categoryService, StoreProductPriceService storeProductPriceService,
                              UserService userService, EntityDtoMapper entityDtoMapper, AuthenticationManager authenticationManager, BCryptPasswordEncoder passwordEncoder) {
        this.productService = productService;
        this.storeService = storeService;
        this.categoryService = categoryService;
        this.storeProductPriceService = storeProductPriceService;
        this.userService = userService;
        this.entityDtoMapper = entityDtoMapper;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    @PutMapping(value = "email")
    @Operation(summary = "Изменить почту текущего пользователя")
    public ResponseEntity<Map<String, String>> changeEmail(@RequestBody @Valid NewEmailDto newEmailDto,
                                                           BindingResult bindingResult, Authentication authentication) {
        String password = newEmailDto.getPassword();
        String newEmail = newEmailDto.getNewEmail();
        userService.changeUserEmail(authentication, authenticationManager, password, newEmail, bindingResult);

        Map<String, String> response = new HashMap<>();
        response.put("new email", newEmail);

        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "password")
    @Operation(summary = "Изменить пароль текущего пользователя")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody @Valid NewPasswordDto newPasswordDto,
                                                              BindingResult bindingResult, Authentication authentication) {
        String oldPassword = newPasswordDto.getOldPassword();
        String newPassword = newPasswordDto.getNewPassword();
        userService.changeUserPassword(authentication, authenticationManager, oldPassword, newPassword, passwordEncoder, bindingResult);

        Map<String, String> response = new HashMap<>();
        response.put("new password", newPassword);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "stores")
    @Operation(summary = "Показать справочник торговых точек")
    public ResponseEntity<List<StoreDto>> getStoresDirectory(@RequestParam int offset,
                                                             @RequestParam(name = "page-size") int pageSize) {
        List<StoreDto> storesDirectory = storeService.getStoresDirectory(offset, pageSize).stream()
                .map(el -> entityDtoMapper.toDto(el, StoreDto.class)).toList();
        return ResponseEntity.ok(storesDirectory);
    }

    @GetMapping(value = "categories")
    @Operation(summary = "Показать справочник категорий товаров")
    public ResponseEntity<List<CategoryDto>> getCategoriesDirectory(@RequestParam int offset,
                                                                    @RequestParam(name = "page-size") int pageSize) {
        List<CategoryDto> categoriesDirectory = categoryService.getCategoriesDirectory(offset, pageSize).stream()
                .map(el -> entityDtoMapper.toDto(el, CategoryDto.class)).toList();
        return ResponseEntity.ok(categoriesDirectory);
    }

    @GetMapping(value = "products")
    @Operation(summary = "Просмотреть список товаров по категориям")
    public ResponseEntity<List<ProductDto>> getProductsByCategory(@RequestParam String category) {
        List<ProductDto> products = productService.getProductsByCategory(category).stream()
                .map(el -> entityDtoMapper.toDto(el, ProductDto.class)).toList();
        return ResponseEntity.ok(products);
    }

    @GetMapping(value = "store-product-prices/{product-id}")
    @Operation(summary = "Отследить динамику цен на определенный товар в заданном периоде")
    public ResponseEntity<List<StoreProductPriceDto>> getPricesDynamics(@PathVariable(name = "product-id") Long id,
                                                                        @RequestParam(name = "date-start") String dateStart,
                                                                        @RequestParam(name = "date-end") String dateEnd) {
        List<StoreProductPriceDto> productPrices = storeProductPriceService.getProductPricesForPeriod(id, dateStart, dateEnd).stream()
                .map(el -> entityDtoMapper.toDto(el, StoreProductPriceDto.class)).toList();
        return ResponseEntity.ok(productPrices);
    }

    @GetMapping(value = "store-product-prices/comparison/{product-id}")
    @Transactional
    @Operation(summary = "Сравненить цены по позициям двух магазинов")
    public ResponseEntity<Map<String, Long>> getStoreProductPricesComparison(@PathVariable(name = "product-id") Long productId,
                                                                             @RequestParam(name = "first-store-id") Long firstStoreId,
                                                                             @RequestParam(name = "second-store-id") Long secondStoreId) {
        Map<String, Long> currentStoreProductPricesByNames = new HashMap<>();
        Map<Long, Long> currentStoreProductPrices = storeProductPriceService.getCurrentStoreProductPrices(productId, firstStoreId, secondStoreId);

        currentStoreProductPrices.forEach((k, v) -> currentStoreProductPricesByNames.put(storeService.getStoreById(k).getName(), v));
        return ResponseEntity.ok(currentStoreProductPricesByNames);
    }

    @GetMapping(value = "store-product-prices/comparison-all/{product-id}")
    @Transactional
    @Operation(summary = "Сравненить цены по позициям всех магазинов")
    public ResponseEntity<Map<String, Long>> getStoreProductPricesComparisonAllStores(@PathVariable(name = "product-id") Long productId) {
        Map<String, Long> allStoresProductPricesByNames = new HashMap<>();
        List<Long> storeIds = storeService.getAllStoreIds();
        Map<Long, Long> allStoresProductPrices = storeProductPriceService.getAllStoresProductPrices(storeIds, productId);

        allStoresProductPrices.forEach((k, v) -> allStoresProductPricesByNames.put(storeService.getStoreById(k).getName(), v));
        return ResponseEntity.ok(allStoresProductPricesByNames);
    }

    @GetMapping(value = "store-product-prices/dynamics/{product-id}")
    @Operation(summary = "Отобразить динамику цен на продукт в одном магазине")
    public ResponseEntity<Map<Date, Long>> provideProductPricesOneStore(@PathVariable(name = "product-id") Long productId,
                                                                        @RequestParam(name = "store-id") Long storeId,
                                                                        @RequestParam int offset,
                                                                        @RequestParam(name = "page-size") int pageSize) {
        Map<Date, Long> productPricesOneStore = storeProductPriceService.getProductPricesOneStore(productId, storeId, offset, pageSize);
        return ResponseEntity.ok(productPricesOneStore);
    }

    @GetMapping(value = "store-product-prices/dynamics-all/{product-id}")
    @Operation(summary = "Отобразить динамику цен на продукт во всех магазинах")
    public ResponseEntity<Map<Date, Long>> provideProductPrices(@PathVariable(name = "product-id") Long productId,
                                                                @RequestParam int offset,
                                                                @RequestParam(name = "page-size") int pageSize) {
        Map<Date, Long> productPrices = storeProductPriceService.getProductPrices(productId, offset, pageSize);
        return ResponseEntity.ok(productPrices);
    }
}
