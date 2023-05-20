package com.ilyaevteev.productmonitoring.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyaevteev.productmonitoring.dto.response.CategoryDto;
import com.ilyaevteev.productmonitoring.dto.response.ProductResponseDto;
import com.ilyaevteev.productmonitoring.dto.response.StoreDto;
import com.ilyaevteev.productmonitoring.dto.response.DynamicsPriceDto;
import com.ilyaevteev.productmonitoring.dto.response.ComparisonPriceDto;
import com.ilyaevteev.productmonitoring.dto.response.DynamicsPricePerPeriodDto;
import com.ilyaevteev.productmonitoring.dto.request.NewEmailDto;
import com.ilyaevteev.productmonitoring.dto.request.NewPasswordDto;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import com.ilyaevteev.productmonitoring.service.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final ObjectMapper objectMapper;
    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder passwordEncoder;

    @PutMapping(value = "email")
    @Operation(summary = "Изменить почту текущего пользователя")
    public ResponseEntity<?> changeEmail(@RequestBody @Valid NewEmailDto newEmailDto,
                                                           BindingResult bindingResult, Authentication authentication) {
        userService.changeUserEmail(authentication, authenticationManager, newEmailDto.getPassword(),
                newEmailDto.getNewEmail(), bindingResult);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping(value = "password")
    @Operation(summary = "Изменить пароль текущего пользователя")
    public ResponseEntity<?> changePassword(@RequestBody @Valid NewPasswordDto newPasswordDto,
                                                              BindingResult bindingResult, Authentication authentication) {
        userService.changeUserPassword(authentication, authenticationManager, newPasswordDto.getOldPassword(),
                newPasswordDto.getNewPassword(), passwordEncoder, bindingResult);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "stores")
    @Operation(summary = "Показать справочник торговых точек")
    public ResponseEntity<List<StoreDto>> getStoresDirectory(@RequestParam int offset,
                                                             @RequestParam(name = "page-size") int pageSize) {
        List<StoreDto> storesDirectory = storeService.getStoresDirectory(offset, pageSize).stream()
                .map(el -> entityDtoMapper.toDto(el, StoreDto.class)).toList();

        return new ResponseEntity<>(storesDirectory, HttpStatus.OK);
    }

    @GetMapping(value = "categories")
    @Operation(summary = "Показать справочник категорий товаров")
    public ResponseEntity<List<CategoryDto>> getCategoriesDirectory(@RequestParam int offset,
                                                                    @RequestParam(name = "page-size") int pageSize) {
        List<CategoryDto> categoriesDirectory = categoryService.getCategoriesDirectory(offset, pageSize).stream()
                .map(el -> entityDtoMapper.toDto(el, CategoryDto.class)).toList();

        return new ResponseEntity<>(categoriesDirectory, HttpStatus.OK);
    }

    @GetMapping(value = "products")
    @Operation(summary = "Просмотреть список товаров по категориям")
    public ResponseEntity<List<ProductResponseDto>> getProductsByCategory(@RequestParam String category) {
        List<ProductResponseDto> products = productService.getProductsByCategory(category).stream()
                .map(el -> entityDtoMapper.toDto(el, ProductResponseDto.class)).toList();

        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping(value = "store-product-prices/{product-id}")
    @Operation(summary = "Отследить динамику цен на определенный товар в заданном периоде")
    public ResponseEntity<List<DynamicsPricePerPeriodDto>> getPricesDynamics(@PathVariable(name = "product-id") Long id,
                                                                             @RequestParam(name = "date-start") String dateStart,
                                                                             @RequestParam(name = "date-end") String dateEnd) {
        List<DynamicsPricePerPeriodDto> productPrices = storeProductPriceService.getProductPricesForPeriod(id, dateStart, dateEnd).stream()
                .map(el -> entityDtoMapper.toDto(el, DynamicsPricePerPeriodDto.class)).toList();

        return new ResponseEntity<>(productPrices, HttpStatus.OK);
    }

    @GetMapping(value = "store-product-prices/comparison/{product-id}")
    @Operation(summary = "Сравненить цены по позициям двух магазинов")
    public ResponseEntity<List<ComparisonPriceDto>> getStoreProductPricesComparison(@PathVariable(name = "product-id") Long productId,
                                                                              @RequestParam(name = "first-store-id") Long firstStoreId,
                                                                              @RequestParam(name = "second-store-id") Long secondStoreId) {
        List<ComparisonPriceDto> currentStoreProductPrices = storeProductPriceService.getCurrentStoreProductPrices(productId,
                firstStoreId, secondStoreId).stream().map(el -> objectMapper.convertValue(el, ComparisonPriceDto.class)).toList();

        return new ResponseEntity<>(currentStoreProductPrices, HttpStatus.OK);
    }

    @GetMapping(value = "store-product-prices/comparison-all/{product-id}")
    @Operation(summary = "Сравненить цены по позициям всех магазинов")
    public ResponseEntity<List<ComparisonPriceDto>> getStoreProductPricesComparisonAllStores(@PathVariable(name = "product-id") Long productId) {
        List<ComparisonPriceDto> allStoresProductPrices = storeProductPriceService.getAllStoresProductPrices(productId).stream()
                .map(el -> objectMapper.convertValue(el, ComparisonPriceDto.class)).toList();

        return new ResponseEntity<>(allStoresProductPrices, HttpStatus.OK);
    }

    @GetMapping(value = "store-product-prices/dynamics/{product-id}")
    @Operation(summary = "Отобразить динамику цен на продукт в одном магазине")
    public ResponseEntity<List<DynamicsPriceDto>> provideProductPricesOneStore(@PathVariable(name = "product-id") Long productId,
                                                                         @RequestParam(name = "store-id") Long storeId,
                                                                         @RequestParam int offset,
                                                                         @RequestParam(name = "page-size") int pageSize) {
        List<DynamicsPriceDto> productPricesOneStore = storeProductPriceService.getProductPricesOneStore(productId, storeId, offset, pageSize).stream()
                .map(el -> objectMapper.convertValue(el, DynamicsPriceDto.class)).toList();

        return new ResponseEntity<>(productPricesOneStore, HttpStatus.OK);
    }

    @GetMapping(value = "store-product-prices/dynamics-all/{product-id}")
    @Operation(summary = "Отобразить динамику цен на продукт во всех магазинах")
    public ResponseEntity<List<DynamicsPriceDto>> provideProductPrices(@PathVariable(name = "product-id") Long productId,
                                                                 @RequestParam int offset,
                                                                 @RequestParam(name = "page-size") int pageSize) {
        List<DynamicsPriceDto> productPrices = storeProductPriceService.getProductPrices(productId, offset, pageSize).stream()
                .map(el -> objectMapper.convertValue(el, DynamicsPriceDto.class)).toList();

        return new ResponseEntity<>(productPrices, HttpStatus.OK);
    }
}
