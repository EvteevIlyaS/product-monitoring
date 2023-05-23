package com.ilyaevteev.productmonitoring.rest.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyaevteev.productmonitoring.dto.request.StoreProductPriceRequestDto;
import com.ilyaevteev.productmonitoring.dto.response.DeletionStoreProductPriceResponseDto;
import com.ilyaevteev.productmonitoring.dto.response.StoreProductPriceResponseDto;
import com.ilyaevteev.productmonitoring.dto.response.UploadDataDto;
import com.ilyaevteev.productmonitoring.service.StoreProductPriceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController(value = "storeProductPriceRestControllerAdmin")
@RequestMapping(value = "/api/v1/admin/store-product-prices")
@RequiredArgsConstructor
public class StoreProductPriceRestController {
    private final StoreProductPriceService storeProductPriceService;

    private final ObjectMapper objectMapper;

    @PostMapping
    @Operation(summary = "Привязать цену к товару в конкретном магазине на текущий момент")
    public ResponseEntity<StoreProductPriceResponseDto> createStoreProductPrice(@RequestBody StoreProductPriceRequestDto storeProductPriceRequestDto) {
        StoreProductPriceResponseDto storeProductPrice = objectMapper.convertValue(storeProductPriceService.addStoreProductPrice(storeProductPriceRequestDto.getPrice(),
                storeProductPriceRequestDto.getProductId(), storeProductPriceRequestDto.getStoreId()), StoreProductPriceResponseDto.class);

        return new ResponseEntity<>(storeProductPrice, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    @Operation(summary = "Удалить цену к товару в конкретном магазине")
    public ResponseEntity<DeletionStoreProductPriceResponseDto> deleteStoreProductPrice(@PathVariable Long id) {
        DeletionStoreProductPriceResponseDto storeProductPrice = objectMapper.convertValue(storeProductPriceService.deleteProductPriceStore(id),
                DeletionStoreProductPriceResponseDto.class);

        return new ResponseEntity<>(storeProductPrice, HttpStatus.OK);
    }

    @PostMapping(value = "/upload")
    @Operation(summary = "Выгрузить информации о ценах в формате csv/xlsx")
    public ResponseEntity<UploadDataDto> uploadPrices(@RequestBody MultipartFile file) {
        UploadDataDto uploadData = objectMapper.convertValue(storeProductPriceService.uploadFilePrices(file), UploadDataDto.class);

        return new ResponseEntity<>(uploadData, HttpStatus.CREATED);
    }
}
