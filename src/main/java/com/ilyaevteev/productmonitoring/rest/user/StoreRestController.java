package com.ilyaevteev.productmonitoring.rest.user;

import com.ilyaevteev.productmonitoring.dto.response.StoreDto;
import com.ilyaevteev.productmonitoring.service.*;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController(value = "storeRestControllerUser")
@RequestMapping(value = "/api/v1/user/stores")
@RequiredArgsConstructor
public class StoreRestController {
    private final StoreService storeService;

    private final EntityDtoMapper entityDtoMapper;

    @GetMapping
    @Operation(summary = "Показать справочник торговых точек")
    public ResponseEntity<List<StoreDto>> getStoresDirectory(@RequestParam int offset,
                                                             @RequestParam(name = "page-size") int pageSize) {
        List<StoreDto> storesDirectory = storeService.getStoresDirectory(offset, pageSize).stream()
                .map(el -> entityDtoMapper.toDto(el, StoreDto.class)).toList();

        return new ResponseEntity<>(storesDirectory, HttpStatus.OK);
    }
}
