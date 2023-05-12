package com.ilyaevteev.productmonitoring.service;

import com.ilyaevteev.productmonitoring.model.Store;

import java.util.List;

public interface StoreService {
    List<Store> getStoresDirectory(int offset, int pageSize);

    Store getStoreById(Long id);

    List<Long> getAllStoreIds();
}
