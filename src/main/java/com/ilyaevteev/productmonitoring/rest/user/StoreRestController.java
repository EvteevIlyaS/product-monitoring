package com.ilyaevteev.productmonitoring.rest.user;

import com.ilyaevteev.productmonitoring.dto.response.StoreDto;
import com.ilyaevteev.productmonitoring.service.*;
import com.ilyaevteev.productmonitoring.util.EntityDtoMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController(value = "storeRestControllerUser")
@RequestMapping(value = "/api/v1/user/stores")
@RequiredArgsConstructor
public class StoreRestController {
    private final StoreService storeService;

    private final EntityDtoMapper entityDtoMapper;

    @GetMapping
    @Operation(summary = "Показать справочник торговых точек")
    public Page<StoreDto> getStoresDirectory(@PageableDefault(size = 20) Pageable pageable) {
        return storeService.getStoresDirectory(pageable)
                .map(el -> entityDtoMapper.toDto(el, StoreDto.class));
    }
}
