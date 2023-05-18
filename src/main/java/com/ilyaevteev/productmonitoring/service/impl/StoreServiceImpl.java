package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.exception.exceptionlist.BadRequestException;
import com.ilyaevteev.productmonitoring.model.Store;
import com.ilyaevteev.productmonitoring.repository.StoreRepository;
import com.ilyaevteev.productmonitoring.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;

    @Override
    public List<Store> getStoresDirectory(int offset, int pageSize) {
        Pageable page = PageRequest.of(offset, pageSize);
        return storeRepository.findAll(page).getContent();
    }

    @Override
    public Store getStoreById(Long id) {
        return storeRepository.findById(id).orElseGet(() -> {
            String message = "No stores found by id: " + id;
            log.error(message);
            throw new BadRequestException(message);
        });
    }

    @Override
    public List<Long> getAllStoreIds() {
        return storeRepository.getAllStoreIds();
    }
}
