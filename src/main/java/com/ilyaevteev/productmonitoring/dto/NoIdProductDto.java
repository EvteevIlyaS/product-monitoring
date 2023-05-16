package com.ilyaevteev.productmonitoring.dto;

import lombok.Data;

@Data
public class NoIdProductDto {
    private String name;

    private CategoryDto category;
}
