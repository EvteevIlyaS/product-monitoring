package com.ilyaevteev.productmonitoring.service;

import com.ilyaevteev.productmonitoring.model.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StoreService {
    Page<Store> getStoresDirectory(Pageable pageable);

    Store getStoreById(Long id);

    List<Long> getAllStoreIds();
}
