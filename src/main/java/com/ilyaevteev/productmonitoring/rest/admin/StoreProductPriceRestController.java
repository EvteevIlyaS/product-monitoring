package com.ilyaevteev.productmonitoring.rest.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyaevteev.productmonitoring.dto.request.StoreProductPriceRequestDto;
import com.ilyaevteev.productmonitoring.dto.response.DeletionStoreProductPriceResponseDto;
import com.ilyaevteev.productmonitoring.dto.response.StoreProductPriceResponseDto;
import com.ilyaevteev.productmonitoring.dto.response.UploadDataDto;
import com.ilyaevteev.productmonitoring.service.StoreProductPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController(value = "storeProductPriceRestControllerAdmin")
@RequestMapping(value = "/api/v1/admin/store-product-prices")
@RequiredArgsConstructor
public class StoreProductPriceRestController {
    private final StoreProductPriceService storeProductPriceService;

    private final ObjectMapper objectMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Привязать цену к товару в конкретном магазине на текущий момент")
    public StoreProductPriceResponseDto createStoreProductPrice(@RequestBody StoreProductPriceRequestDto storeProductPriceRequestDto) {
        return objectMapper.convertValue(storeProductPriceService.addStoreProductPrice(storeProductPriceRequestDto.getPrice(),
                storeProductPriceRequestDto.getProductId(), storeProductPriceRequestDto.getStoreId()), StoreProductPriceResponseDto.class);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Удалить цену к товару в конкретном магазине")
    public DeletionStoreProductPriceResponseDto deleteStoreProductPrice(@PathVariable Long id) {
        return objectMapper.convertValue(storeProductPriceService.deleteProductPriceStore(id),
                DeletionStoreProductPriceResponseDto.class);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Выгрузить информации о ценах в формате csv/xlsx")
    public UploadDataDto uploadPrices(@RequestBody MultipartFile file) {
        return objectMapper.convertValue(storeProductPriceService.uploadFilePrices(file), UploadDataDto.class);
    }
}
