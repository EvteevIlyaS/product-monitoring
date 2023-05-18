package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.model.Store;
import com.ilyaevteev.productmonitoring.repository.StoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StoreServiceImplTest {
    @Mock
    private StoreRepository storeRepository;

    @InjectMocks
    private StoreServiceImpl storeService;

    @Test
    void getStoresDirectory_checkReturnedValue() {
        Store mall = new Store();
        Store supermarket = new Store();
        List<Store> stores = new ArrayList<>();
        stores.add(mall);
        stores.add(supermarket);
        int offset = 0;
        int pageSize = 2;
        Page<Store> page = new PageImpl<>(stores);
        when(storeRepository.findAll((Pageable) any())).thenReturn(page);

        List<Store> storesRes = storeService.getStoresDirectory(offset, pageSize);

        assertThat(storesRes).isEqualTo(page.getContent());
    }

    @Test
    void getStoreById_checkReturnedValue() {
        Long id = 1L;
        Store store = new Store();
        Optional<Store> optionalStore = Optional.of(store);
        when(storeRepository.findById(id)).thenReturn(optionalStore);

        Store storeRes = storeService.getStoreById(id);

        assertThat(storeRes).isEqualTo(store);
    }

    @Test
    void getStoreById_checkThrowException() {
        Long id = 1L;
        Optional<Store> optionalStore = Optional.empty();
        when(storeRepository.findById(id)).thenReturn(optionalStore);

        assertThatThrownBy(() -> storeService.getStoreById(id))
                .hasMessage("No stores found by id: " + id);
    }

    @Test
    void getAllStoreIds() {
        List<Long> storeIds = new ArrayList<>();
        Long firstStoreId = 1L;
        Long secondStoreId = 2L;
        storeIds.add(firstStoreId);
        storeIds.add(secondStoreId);
        when(storeRepository.getAllStoreIds()).thenReturn(storeIds);

        List<Long> storeIdsRes = storeService.getAllStoreIds();

        assertThat(storeIdsRes).isEqualTo(storeIds);
    }
}