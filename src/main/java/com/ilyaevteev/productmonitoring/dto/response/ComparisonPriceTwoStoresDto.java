package com.ilyaevteev.productmonitoring.dto.response;

import lombok.Data;

@Data
public class ComparisonPriceTwoStoresDto {
    private ComparisonPriceDto firstStore;

    private ComparisonPriceDto secondStore;

}
