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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(value = "/api/v1/user/")
@RequiredArgsConstructor
public class UserRestController {
    private final ProductService productService;
    private final StoreService storeService;
    private final CategoryService categoryService;
    private final StoreProductPriceService storeProductPriceService;
    private final UserService userService;

    private final EntityDtoMapper entityDtoMapper;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;

    @PutMapping(value = "email")
    @Operation(summary = "Изменить почту текущего пользователя")
    public ResponseEntity<?> changeEmail(@RequestBody @Valid NewEmailDto newEmailDto,
                                                           BindingResult bindingResult, Authentication authentication) {
        userService.changeUserEmail(authentication, authenticationManager, newEmailDto.getPassword(),
                newEmailDto.getNewEmail(), bindingResult);

        return ResponseEntity.noContent().build();
    }

    @PutMapping(value = "password")
    @Operation(summary = "Изменить пароль текущего пользователя")
    public ResponseEntity<?> changePassword(@RequestBody @Valid NewPasswordDto newPasswordDto,
                                                              BindingResult bindingResult, Authentication authentication) {
        userService.changeUserPassword(authentication, authenticationManager, newPasswordDto.getOldPassword(),
                newPasswordDto.getNewPassword(), passwordEncoder, bindingResult);

        return ResponseEntity.noContent().build();
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
    @Operation(summary = "Сравненить цены по позициям двух магазинов")
    public ResponseEntity<Map<String, Long>> getStoreProductPricesComparison(@PathVariable(name = "product-id") Long productId,
                                                                             @RequestParam(name = "first-store-id") Long firstStoreId,
                                                                             @RequestParam(name = "second-store-id") Long secondStoreId) {
        Map<String, Long> currentStoreProductPrices = storeProductPriceService.getCurrentStoreProductPrices(productId, firstStoreId, secondStoreId);
        return ResponseEntity.ok(currentStoreProductPrices);
    }

    @GetMapping(value = "store-product-prices/comparison-all/{product-id}")
    @Operation(summary = "Сравненить цены по позициям всех магазинов")
    public ResponseEntity<Map<String, Long>> getStoreProductPricesComparisonAllStores(@PathVariable(name = "product-id") Long productId) {
        Map<String, Long> allStoresProductPrices = storeProductPriceService.getAllStoresProductPrices(productId);
        return ResponseEntity.ok(allStoresProductPrices);
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
