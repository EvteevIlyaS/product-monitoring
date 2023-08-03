package com.ilyaevteev.productmonitoring.rest.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ilyaevteev.productmonitoring.dto.request.StoreDtoRequest;
import com.ilyaevteev.productmonitoring.dto.response.StoreDtoResponse;
import com.ilyaevteev.productmonitoring.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController(value = "storeRestControllerAdmin")
@RequestMapping(value = "/api/v1/admin/stores")
@RequiredArgsConstructor
public class StoreRestController {
    private final StoreService storeService;

    private final ObjectMapper objectMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Добавить магазин")
    public StoreDtoResponse createStore(@RequestBody StoreDtoRequest storeDtoRequest) {
        return objectMapper.convertValue(storeService.addStore(storeDtoRequest.getName(), storeDtoRequest.getAddress()),
                StoreDtoResponse.class);
    }
}
