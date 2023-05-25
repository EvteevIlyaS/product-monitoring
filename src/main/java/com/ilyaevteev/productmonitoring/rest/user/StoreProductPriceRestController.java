package com.ilyaevteev.productmonitoring.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyaevteev.productmonitoring.dto.response.ComparisonPriceDto;
import com.ilyaevteev.productmonitoring.dto.response.ComparisonPriceTwoStoresDto;
import com.ilyaevteev.productmonitoring.dto.response.DynamicsPriceDto;
import com.ilyaevteev.productmonitoring.dto.response.DynamicsPricePerPeriodDto;
import com.ilyaevteev.productmonitoring.service.*;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController(value = "storeProductPriceRestControllerUser")
@RequestMapping(value = "/api/v1/user/store-product-prices")
@RequiredArgsConstructor
public class StoreProductPriceRestController {
    private final StoreProductPriceService storeProductPriceService;

    private final EntityDtoMapper entityDtoMapper;
    private final ObjectMapper objectMapper;

    @GetMapping(value = "/{product-id}")
    @Operation(summary = "Отследить динамику цен на определенный товар в заданном периоде")
    public Page<DynamicsPricePerPeriodDto> getPricesDynamics(@PathVariable(name = "product-id") Long id,
                                                                             @RequestParam(name = "date-start") String dateStart,
                                                                             @RequestParam(name = "date-end") String dateEnd,
                                                                             Pageable pageable) {
        return storeProductPriceService.getProductPricesForPeriod(id, dateStart, dateEnd, pageable)
                .map(el -> entityDtoMapper.toDto(el, DynamicsPricePerPeriodDto.class));
    }

    @GetMapping(value = "/comparison/{product-id}")
    @Operation(summary = "Сравненить цены по позициям двух магазинов")
    public ComparisonPriceTwoStoresDto getStoreProductPricesComparison(@PathVariable(name = "product-id") Long productId,
                                                                                    @RequestParam(name = "first-store-id") Long firstStoreId,
                                                                                    @RequestParam(name = "second-store-id") Long secondStoreId) {
        return objectMapper.convertValue(storeProductPriceService.getCurrentStoreProductPrices(productId,
                firstStoreId, secondStoreId), ComparisonPriceTwoStoresDto.class);
    }

    @GetMapping(value = "/comparison-all/{product-id}")
    @Operation(summary = "Сравненить цены по позициям всех магазинов")
    public Page<ComparisonPriceDto> getStoreProductPricesComparisonAllStores(@PathVariable(name = "product-id") Long productId, Pageable pageable) {
        return storeProductPriceService.getAllStoresProductPrices(productId, pageable)
                .map(el -> objectMapper.convertValue(el, ComparisonPriceDto.class));
    }

    @GetMapping(value = "/dynamics-all/{product-id}")
    @Operation(summary = "Отобразить динамику цен на продукт во всех магазинах")
    public Page<DynamicsPriceDto> provideProductPrices(@PathVariable(name = "product-id") Long productId,
                                                                       Pageable pageable) {
        return storeProductPriceService.getProductPrices(productId, pageable)
                .map(el -> objectMapper.convertValue(el, DynamicsPriceDto.class));
    }

    @GetMapping(value = "/dynamics/{product-id}")
    @Operation(summary = "Отобразить динамику цен на продукт в одном магазине")
    public Page<DynamicsPriceDto> provideProductPricesOneStore(@PathVariable(name = "product-id") Long productId,
                                                                               @RequestParam(name = "store-id") Long storeId,
                                                                               Pageable pageable) {
        return storeProductPriceService.getProductPricesOneStore(productId, storeId, pageable)
                .map(el -> objectMapper.convertValue(el, DynamicsPriceDto.class));
    }
}
