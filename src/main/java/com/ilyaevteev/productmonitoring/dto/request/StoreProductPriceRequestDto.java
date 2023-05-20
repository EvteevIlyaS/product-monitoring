package com.ilyaevteev.productmonitoring.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ilyaevteev.productmonitoring.dto.response.StoreDto;
import com.ilyaevteev.productmonitoring.dto.response.ProductResponseDto;
import lombok.Data;

import java.util.Date;

@Data
public class StoreProductPriceRequestDto {
    private Long price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    private StoreDto store;

    private ProductResponseDto product;
}
