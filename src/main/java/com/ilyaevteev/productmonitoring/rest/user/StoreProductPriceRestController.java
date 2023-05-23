package com.ilyaevteev.productmonitoring.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyaevteev.productmonitoring.dto.response.ComparisonPriceDto;
import com.ilyaevteev.productmonitoring.dto.response.DynamicsPriceDto;
import com.ilyaevteev.productmonitoring.dto.response.DynamicsPricePerPeriodDto;
import com.ilyaevteev.productmonitoring.service.*;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController(value = "storeProductPriceRestControllerUser")
@RequestMapping(value = "/api/v1/user/store-product-prices")
@RequiredArgsConstructor
public class StoreProductPriceRestController {
    private final StoreProductPriceService storeProductPriceService;

    private final EntityDtoMapper entityDtoMapper;
    private final ObjectMapper objectMapper;

    @GetMapping(value = "/{product-id}")
    @Operation(summary = "Отследить динамику цен на определенный товар в заданном периоде")
    public ResponseEntity<List<DynamicsPricePerPeriodDto>> getPricesDynamics(@PathVariable(name = "product-id") Long id,
                                                                             @RequestParam(name = "date-start") String dateStart,
                                                                             @RequestParam(name = "date-end") String dateEnd) {
        List<DynamicsPricePerPeriodDto> productPrices = storeProductPriceService.getProductPricesForPeriod(id, dateStart, dateEnd).stream()
                .map(el -> entityDtoMapper.toDto(el, DynamicsPricePerPeriodDto.class)).toList();

        return new ResponseEntity<>(productPrices, HttpStatus.OK);
    }

    @GetMapping(value = "/comparison/{product-id}")
    @Operation(summary = "Сравненить цены по позициям двух магазинов")
    public ResponseEntity<List<ComparisonPriceDto>> getStoreProductPricesComparison(@PathVariable(name = "product-id") Long productId,
                                                                                    @RequestParam(name = "first-store-id") Long firstStoreId,
                                                                                    @RequestParam(name = "second-store-id") Long secondStoreId) {
        List<ComparisonPriceDto> currentStoreProductPrices = storeProductPriceService.getCurrentStoreProductPrices(productId,
                firstStoreId, secondStoreId).stream().map(el -> objectMapper.convertValue(el, ComparisonPriceDto.class)).toList();

        return new ResponseEntity<>(currentStoreProductPrices, HttpStatus.OK);
    }

    @GetMapping(value = "/comparison-all/{product-id}")
    @Operation(summary = "Сравненить цены по позициям всех магазинов")
    public ResponseEntity<List<ComparisonPriceDto>> getStoreProductPricesComparisonAllStores(@PathVariable(name = "product-id") Long productId) {
        List<ComparisonPriceDto> allStoresProductPrices = storeProductPriceService.getAllStoresProductPrices(productId).stream()
                .map(el -> objectMapper.convertValue(el, ComparisonPriceDto.class)).toList();

        return new ResponseEntity<>(allStoresProductPrices, HttpStatus.OK);
    }

    @GetMapping(value = "/dynamics/{product-id}")
    @Operation(summary = "Отобразить динамику цен на продукт в одном магазине")
    public ResponseEntity<List<DynamicsPriceDto>> provideProductPricesOneStore(@PathVariable(name = "product-id") Long productId,
                                                                               @RequestParam(name = "store-id") Long storeId,
                                                                               @RequestParam int offset,
                                                                               @RequestParam(name = "page-size") int pageSize) {
        List<DynamicsPriceDto> productPricesOneStore = storeProductPriceService.getProductPricesOneStore(productId, storeId, offset, pageSize).stream()
                .map(el -> objectMapper.convertValue(el, DynamicsPriceDto.class)).toList();

        return new ResponseEntity<>(productPricesOneStore, HttpStatus.OK);
    }

    @GetMapping(value = "/dynamics-all/{product-id}")
    @Operation(summary = "Отобразить динамику цен на продукт во всех магазинах")
    public ResponseEntity<List<DynamicsPriceDto>> provideProductPrices(@PathVariable(name = "product-id") Long productId,
                                                                       @RequestParam int offset,
                                                                       @RequestParam(name = "page-size") int pageSize) {
        List<DynamicsPriceDto> productPrices = storeProductPriceService.getProductPrices(productId, offset, pageSize).stream()
                .map(el -> objectMapper.convertValue(el, DynamicsPriceDto.class)).toList();

        return new ResponseEntity<>(productPrices, HttpStatus.OK);
    }
}
