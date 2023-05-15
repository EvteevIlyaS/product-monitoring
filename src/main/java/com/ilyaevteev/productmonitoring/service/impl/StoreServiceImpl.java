package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.model.Store;
import com.ilyaevteev.productmonitoring.repository.StoreRepository;
import com.ilyaevteev.productmonitoring.service.StoreService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;

    @Autowired
    public StoreServiceImpl(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    @Override
    public List<Store> getStoresDirectory(int offset, int pageSize) {
        Pageable page = PageRequest.of(offset, pageSize);
        return storeRepository.findAll(page).getContent();
    }

    @Override
    public Store getStoreById(Long id) {
        Optional<Store> store = storeRepository.findById(id);

        if (store.isEmpty()) {
            String message = "No stores found by id: " + id;
            log.error(message);
            throw new RuntimeException(message);
        }

        return store.get();
    }

    @Override
    public List<Long> getAllStoreIds() {
        return storeRepository.findAll().stream().map(Store::getId).toList();
    }
}
