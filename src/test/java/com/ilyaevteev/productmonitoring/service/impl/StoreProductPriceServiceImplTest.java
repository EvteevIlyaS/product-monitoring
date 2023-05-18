package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.model.Store;
import com.ilyaevteev.productmonitoring.model.StoreProductPrice;
import com.ilyaevteev.productmonitoring.repository.StoreProductPricesRepository;
import com.ilyaevteev.productmonitoring.service.ProductService;
import com.ilyaevteev.productmonitoring.service.StoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StoreProductPriceServiceImplTest {
    @Mock
    private StoreProductPricesRepository storeProductPricesRepository;

    @Mock
    private StoreService storeService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private StoreProductPriceServiceImpl storeProductPriceService;

    @Test
    void addStoreProductPrice_checkMethodInvocation() {
        StoreProductPrice storeProductPrice = new StoreProductPrice();

        storeProductPriceService.addStoreProductPrice(storeProductPrice);

        verify(storeProductPricesRepository, times(1)).save(storeProductPrice);
    }

    @Test
    void addStoreProductPrice_checkThrowException() {
        StoreProductPrice storeProductPrice = new StoreProductPrice();
        when(storeProductPricesRepository.save(storeProductPrice)).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> storeProductPriceService.addStoreProductPrice(storeProductPrice))
                .hasMessage("Wrong store product price");
    }

    @Test
    void getProductPricesForPeriod_checkMethodInvocation() {
        Long id = 1L;
        String dateStart = "2023-01-01";
        String dateEnd = "2023-01-02";

        storeProductPriceService.getProductPricesForPeriod(id, dateStart, dateEnd);

        verify(storeProductPricesRepository, times(1))
                .findAllByProductIdAndDateBetweenOrderByDate(anyLong(), any(), any());
    }

    @Test
    void getProductPricesForPeriod_checkThrowException() {
        Long id = 1L;
        String dateStart = "2023-01-01";
        String dateEnd = "2023-01-02";
        when(storeProductPricesRepository.findAllByProductIdAndDateBetweenOrderByDate(anyLong(), any(), any()))
                .thenThrow(new RuntimeException());

        assertThatThrownBy(() -> storeProductPriceService.getProductPricesForPeriod(id, dateStart, dateEnd))
                .hasMessage("Wrong store product price");
    }

    @Test
    void getCurrentStoreProductPrices_checkReturnedValue() {
        Map<String, Long> storeProductPrice = new HashMap<>();
        Long productId = 1L;
        Long firstStoreId = 1L;
        Long secondStoreId = 2L;
        Store firstStore = new Store();
        Store secondStore = new Store();
        String firstStoreName = "mall";
        String secondStoreName = "supermarket";
        firstStore.setName(firstStoreName);
        secondStore.setName(secondStoreName);
        storeProductPrice.put(firstStoreName, null);
        storeProductPrice.put(secondStoreName, null);
        when(storeService.getStoreById(firstStoreId)).thenReturn(firstStore);
        when(storeService.getStoreById(secondStoreId)).thenReturn(secondStore);
        when(storeProductPricesRepository.getFirstByProductIdAndStoreIdOrderByDateDesc(anyLong(), anyLong()))
                .thenReturn(new StoreProductPrice());

        Map<String, Long> storeProductPriceRes = storeProductPriceService
                .getCurrentStoreProductPrices(productId, firstStoreId, secondStoreId);

        assertThat(storeProductPriceRes).isEqualTo(storeProductPrice);
    }

    @Test
    void getCurrentStoreProductPrices_checkThrowException() {
        Long productId = 1L;
        Long firstStoreId = 1L;
        Long secondStoreId = 2L;

        when(storeProductPricesRepository.getFirstByProductIdAndStoreIdOrderByDateDesc(anyLong(), anyLong()))
                .thenReturn(null);

        assertThatThrownBy(() -> storeProductPriceService.getCurrentStoreProductPrices(productId, firstStoreId, secondStoreId))
                .hasMessage("Products not found");
    }

    @Test
    void getAllStoresProductPrices_checkReturnedValue() {
        Map<String, Long> storeProductPrice = new HashMap<>();
        Store store = new Store();
        String storeName = "mall";
        store.setName(storeName);
        Long productId = 1L;
        Long storeId = 1L;
        List<Long> storeIds = List.of(storeId);
        storeProductPrice.put(storeName, null);
        when(storeService.getAllStoreIds()).thenReturn(storeIds);
        when(storeService.getStoreById(anyLong())).thenReturn(store);
        when(storeProductPricesRepository.getFirstByProductIdAndStoreIdOrderByDateDesc(anyLong(), anyLong()))
                .thenReturn(new StoreProductPrice());

        Map<String, Long> storeProductPriceRes = storeProductPriceService
                .getAllStoresProductPrices(productId);

        assertThat(storeProductPriceRes).isEqualTo(storeProductPrice);
    }

    @Test
    void getProductPrices_checkReturnedValue() {
        Map<Date, Long> productPrices = new TreeMap<>();
        Long id = 1L;
        int offset = 0;
        int pageSize = 2;
        Pageable page = PageRequest.of(offset, pageSize);
        StoreProductPrice storeProductPrice = new StoreProductPrice();
        storeProductPrice.setDate(new Date());
        storeProductPrice.setPrice(100L);
        List<StoreProductPrice> storeProductPrices = List.of(storeProductPrice);
        productPrices.put(storeProductPrice.getDate(), storeProductPrice.getPrice());
        when(storeProductPricesRepository.findAllByProductIdOrderByDate(id, page)).thenReturn(storeProductPrices);

        Map<Date, Long> productPricesRes = storeProductPriceService.getProductPrices(id, offset, pageSize);

        assertThat(productPricesRes).isEqualTo(productPrices);
    }

    @Test
    void getProductPricesOneStore_checkReturnedValue() {
        Map<Date, Long> productPrices = new TreeMap<>();
        Long productId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int pageSize = 2;
        Pageable page = PageRequest.of(offset, pageSize);
        StoreProductPrice storeProductPrice = new StoreProductPrice();
        storeProductPrice.setDate(new Date());
        storeProductPrice.setPrice(100L);
        List<StoreProductPrice> storeProductPrices = List.of(storeProductPrice);
        productPrices.put(storeProductPrice.getDate(), storeProductPrice.getPrice());
        when(storeProductPricesRepository.findAllByProductIdAndStoreIdOrderByDate(productId, storeId, page)).thenReturn(storeProductPrices);

        Map<Date, Long> productPricesRes = storeProductPriceService.getProductPricesOneStore(productId, storeId, offset, pageSize);

        assertThat(productPricesRes).isEqualTo(productPrices);
    }

    @Test
    void uploadFilePrices_checkMethodInvocation() {
        String dataLine = "storeId,productId,price\n1,3,100\n2,2,200\n3,1,300";
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "prices-data.csv",
                "text/csv",
                dataLine.getBytes());
        storeProductPriceService.uploadFilePrices(multipartFile);

        verify(storeProductPricesRepository, times(1)).saveAll(anyIterable());
    }

    @Test
    void uploadFilePrices_checkFirstThrowException() {
        String dataLine = "storeId,productId,price\n1,3,100\n2,2,200\n3,1,300";
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "prices-data.csv",
                "text/csv",
                dataLine.getBytes());
        when(storeProductPricesRepository.saveAll(anyIterable())).thenThrow(new RuntimeException(""));

        assertThatThrownBy(() -> storeProductPriceService.uploadFilePrices(multipartFile))
                .hasMessage("Fail to store csv data");
    }

    @Test
    void uploadFilePrices_checkSecondThrowException() {
        String dataLine = "storeId,productId,price\n1,3,100\n2,2,200\n3,1,300";
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "prices-data.csv",
                "odt",
                dataLine.getBytes());

        assertThatThrownBy(() -> storeProductPriceService.uploadFilePrices(multipartFile))
                .hasMessage("Wrong data format");

    }

    @Test
    void uploadFilePrices_checkThirdThrowException() {
        String dataLine = "storeId,productId,price\n1,3,100\n2,2,200\n3,1,300";
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "prices-data.csv",
                "text/csv",
                dataLine.getBytes());
        when(storeService.getStoreById(anyLong())).thenThrow(new RuntimeException("No products found by id"));

        assertThatThrownBy(() -> storeProductPriceService.uploadFilePrices(multipartFile))
                .hasMessage("Fail to store csv data");
    }
}