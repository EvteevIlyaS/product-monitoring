package com.ilyaevteev.productmonitoring.dto.request;

import lombok.Data;

@Data
public class EditionProductDto {
    private Long id;

    private String name;

    private Long categoryId;
}
