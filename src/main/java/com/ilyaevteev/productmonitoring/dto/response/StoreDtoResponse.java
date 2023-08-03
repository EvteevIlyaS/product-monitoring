package com.ilyaevteev.productmonitoring.dto.response;

import lombok.Data;

@Data
public class StoreDtoResponse {
    private Long id;

    private String name;

    private String addressCity;

    private String addressStreet;

    private String addressHouse;

    private String addressPostcode;
}




