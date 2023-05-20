package com.ilyaevteev.productmonitoring.dto.response;

import lombok.Data;

@Data
public class ComparisonPriceDto {
    String store;

    Long price;
}
