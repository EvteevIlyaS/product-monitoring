package com.ilyaevteev.productmonitoring.dto.request;

import lombok.Data;

@Data
public class ProductRequestDto {
    private String name;

    private Long categoryId;
}
