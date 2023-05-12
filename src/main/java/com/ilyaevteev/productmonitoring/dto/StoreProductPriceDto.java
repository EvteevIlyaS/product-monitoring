package com.ilyaevteev.productmonitoring.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class StoreProductPriceDto {
    private long price;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;

    private StoreDto store;

    private ProductDto product;
}
