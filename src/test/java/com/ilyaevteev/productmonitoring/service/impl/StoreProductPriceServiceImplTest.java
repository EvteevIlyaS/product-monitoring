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
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
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
    void addStoreProductPrice_checkReturnedValue() {
        Long id = 1L;
        Long price = 100L;
        Long productId = 1L;
        Long storeId = 1L;
        StoreProductPrice storeProductPrice = new StoreProductPrice();
        storeProductPrice.setId(id);
        storeProductPrice.setPrice(price);
        Map<String, String> productPrice = Map.of("id", id.toString(), "price", price.toString());
        when(storeProductPricesRepository.save(any())).thenReturn(storeProductPrice);

        Map<String, String> productPriceRes = storeProductPriceService.addStoreProductPrice(price, productId, storeId);

        verify(storeProductPricesRepository, times(1)).save(any());
        assertThat(productPriceRes).isEqualTo(productPrice);
    }

    @Test
    void addStoreProductPrice_checkThrowException() {
        Long price = 100L;
        Long productId = 1L;
        Long storeId = 1L;

        when(storeProductPricesRepository.save(any())).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> storeProductPriceService.addStoreProductPrice(price, storeId, productId))
                .hasMessage("Wrong store product price");
    }

    @Test
    void deleteProductPriceStore_checkMethodInvocation() {
        Long id = 1L;

        storeProductPriceService.deleteProductPriceStore(id);

        verify(storeProductPricesRepository, times(1)).deleteById(id);
    }

    @Test
    void getProductPricesForPeriod_checkMethodInvocation() {
        Long id = 1L;
        String dateStart = "2023-01-01";
        String dateEnd = "2023-01-02";
        Pageable pageable = PageRequest.of(0, 3);

        storeProductPriceService.getProductPricesForPeriod(id, dateStart, dateEnd, pageable);

        verify(storeProductPricesRepository, times(1))
                .findAllByProductIdAndDateBetweenOrderByDate(anyLong(), any(), any(), any());
    }

    @Test
    void getProductPricesForPeriod_checkThrowException() {
        Long id = 1L;
        String dateStart = "2023-01-01";
        String dateEnd = "2023-01-02";
        Pageable pageable = PageRequest.of(0, 3);
        when(storeProductPricesRepository.findAllByProductIdAndDateBetweenOrderByDate(anyLong(), any(), any(), any()))
                .thenThrow(new RuntimeException());

        assertThatThrownBy(() -> storeProductPriceService.getProductPricesForPeriod(id, dateStart, dateEnd, pageable))
                .hasMessage("Wrong store product price");
    }

    @Test
    void getCurrentStoreProductPrices_checkReturnedValue() {
        Map<String, Map<String, String>> storeProductPrices;
        Long productId = 1L;
        Long firstStoreId = 1L;
        Long secondStoreId = 2L;
        Store firstStore = new Store();
        Store secondStore = new Store();
        String firstStoreName = "mall";
        String secondStoreName = "supermarket";
        firstStore.setName(firstStoreName);
        secondStore.setName(secondStoreName);
        StoreProductPrice storeProductPrice = new StoreProductPrice();
        storeProductPrice.setPrice(100L);
        storeProductPrices = Map.of("firstStore", Map.of("store", firstStoreName, "price", "100"),
                "secondStore", Map.of( "store", secondStoreName, "price", "100"));
        when(storeService.getStoreById(firstStoreId)).thenReturn(firstStore);
        when(storeService.getStoreById(secondStoreId)).thenReturn(secondStore);
        when(storeProductPricesRepository.getFirstByProductIdAndStoreIdOrderByDateDesc(anyLong(), anyLong()))
                .thenReturn(storeProductPrice);

        Map<String, Map<String, String>> storeProductPriceRes = storeProductPriceService
                .getCurrentStoreProductPrices(productId, firstStoreId, secondStoreId);

        assertThat(storeProductPriceRes).isEqualTo(storeProductPrices);
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
        List<Map<String, String>> storeProductPrices;
        Store store = new Store();
        String storeName = "mall";
        store.setName(storeName);
        Long productId = 1L;
        Long storeId = 1L;
        StoreProductPrice storeProductPrice = new StoreProductPrice();
        storeProductPrice.setPrice(100L);
        List<Long> storeIds = List.of(storeId);
        storeProductPrices = List.of(Map.of("store", storeName, "price", "100"));
        Pageable pageable = PageRequest.of(0, 1);
        when(storeService.getAllStoreIds()).thenReturn(storeIds);
        when(storeService.getStoreById(anyLong())).thenReturn(store);
        when(storeProductPricesRepository.getFirstByProductIdAndStoreIdOrderByDateDesc(anyLong(), anyLong()))
                .thenReturn(storeProductPrice);

        Page<Map<String, String>> storeProductPriceRes = storeProductPriceService
                .getAllStoresProductPrices(productId, pageable);

        assertThat(storeProductPriceRes.getContent()).isEqualTo(storeProductPrices);
    }

    @Test
    void getProductPrices_checkReturnedValue() {
        List<Map<String, String>> productPrices;
        Long id = 1L;
        int offset = 0;
        int pageSize = 2;
        Pageable pageable = PageRequest.of(offset, pageSize);
        StoreProductPrice storeProductPrice = new StoreProductPrice();
        storeProductPrice.setDate(new Date());
        storeProductPrice.setPrice(100L);
        List<StoreProductPrice> storeProductPrices = List.of(storeProductPrice);
        productPrices = List.of(Map.of("date", storeProductPrice.getDate().toString(),
                "price", storeProductPrice.getPrice().toString()));
        when(storeProductPricesRepository.findAllByProductIdOrderByDate(id, pageable)).thenReturn(storeProductPrices);

        Page<Map<String, String>> productPricesRes = storeProductPriceService.getProductPrices(id, pageable);

        assertThat(productPricesRes.getContent()).isEqualTo(productPrices);
    }

    @Test
    void getProductPricesOneStore_checkReturnedValue() {
        List<Map<String, String>> productPrices;
        Long productId = 1L;
        Long storeId = 1L;
        int offset = 0;
        int pageSize = 1;
        Pageable pageable = PageRequest.of(offset, pageSize);
        StoreProductPrice storeProductPrice = new StoreProductPrice();
        storeProductPrice.setDate(new Date());
        storeProductPrice.setPrice(100L);
        List<StoreProductPrice> storeProductPrices = List.of(storeProductPrice);
        productPrices = List.of(Map.of("date", storeProductPrice.getDate().toString(),
                "price", storeProductPrice.getPrice().toString()));
        when(storeProductPricesRepository.findAllByProductIdAndStoreIdOrderByDate(productId, storeId, pageable)).thenReturn(storeProductPrices);

        Page<Map<String, String>> productPricesRes = storeProductPriceService.getProductPricesOneStore(productId, storeId, pageable);

        assertThat(productPricesRes.getContent()).isEqualTo(productPrices);
    }

    @Test
    void uploadFilePrices_checkMethodInvocationCSV() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "prices-data.csv",
                "text/csv",
                new ClassPathResource("upload-data/prices-data.csv").getInputStream());
        storeProductPriceService.uploadFilePrices(multipartFile);

        verify(storeProductPricesRepository, times(1)).saveAll(anyIterable());
    }

    @Test
    void uploadFilePrices_checkMethodInvocationXLSX() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "prices-data.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new ClassPathResource("upload-data/prices-data.xlsx").getInputStream());
        storeProductPriceService.uploadFilePrices(multipartFile);

        verify(storeProductPricesRepository, times(1)).saveAll(anyIterable());
    }

    @Test
    void uploadFilePrices_checkFirstThrowException() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "prices-data.csv",
                "text/csv",
                new ClassPathResource("upload-data/prices-data.csv").getInputStream());
        when(storeProductPricesRepository.saveAll(anyIterable())).thenThrow(new RuntimeException(""));

        assertThatThrownBy(() -> storeProductPriceService.uploadFilePrices(multipartFile))
                .hasMessage("Fail to store data");
    }

    @Test
    void uploadFilePrices_checkSecondThrowException() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "prices-data.csv",
                "odt",
                new ClassPathResource("upload-data/prices-data.csv").getInputStream());

        assertThatThrownBy(() -> storeProductPriceService.uploadFilePrices(multipartFile))
                .hasMessage("Wrong data format");

    }

    @Test
    void uploadFilePrices_checkThirdThrowException() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "prices-data.csv",
                "text/csv",
                new ClassPathResource("upload-data/prices-data.csv").getInputStream());
        when(storeService.getStoreById(anyLong())).thenThrow(new RuntimeException("No products found by id"));

        assertThatThrownBy(() -> storeProductPriceService.uploadFilePrices(multipartFile))
                .hasMessage("Fail to store data");
    }
}