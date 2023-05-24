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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
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
        List<Store> stores = Arrays.asList(new Store(), new Store());
        Page<Store> page = new PageImpl<>(stores);
        Pageable pageable = PageRequest.of(0, 2);
        when(storeRepository.findAll((Pageable) any())).thenReturn(page);

        Page<Store> storesRes = storeService.getStoresDirectory(pageable);

        assertThat(storesRes).isEqualTo(page);
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
        List<Long> storeIds = Arrays.asList(1L, 2L);
        when(storeRepository.getAllStoreIds()).thenReturn(storeIds);

        List<Long> storeIdsRes = storeService.getAllStoreIds();

        assertThat(storeIdsRes).isEqualTo(storeIds);
    }
}