package com.ilyaevteev.productmonitoring.dto.request;

import lombok.Data;

@Data
public class StoreProductPriceRequestDto {
    private Long price;

    private Long storeId;

    private Long productId;
}
