package com.ilyaevteev.productmonitoring.service;

import com.ilyaevteev.productmonitoring.model.StoreProductPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface StoreProductPriceService {
    Map<String, String> addStoreProductPrice(Long price, Long storeId, Long productId);

    Map<String, String> deleteProductPriceStore(Long id);

    Page<StoreProductPrice> getProductPricesForPeriod(Long id, String dateStart, String dateEnd, Pageable pageable);

    Map<String, Map<String, String>> getCurrentStoreProductPrices(Long productId, Long firstStoreId, Long secondStoreId);

    Page<Map<String, String>> getAllStoresProductPrices(Long productId, Pageable pageable);

    Page<Map<String, String>> getProductPrices(Long id, Pageable pageable);

    Page<Map<String, String>> getProductPricesOneStore(Long productId, Long storeId, Pageable pageable);

    Map<String, String> uploadFilePrices(MultipartFile file);

}
