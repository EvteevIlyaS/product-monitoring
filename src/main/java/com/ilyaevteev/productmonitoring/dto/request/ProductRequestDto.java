package com.ilyaevteev.productmonitoring.dto.request;

import com.ilyaevteev.productmonitoring.dto.response.CategoryDto;
import lombok.Data;

@Data
public class ProductRequestDto {
    private String name;

    private CategoryDto category;
}
