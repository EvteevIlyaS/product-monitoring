package com.ilyaevteev.productmonitoring.service;

import com.ilyaevteev.productmonitoring.model.StoreProductPrice;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface StoreProductPriceService {
    Map<String, String> addStoreProductPrice(StoreProductPrice storeProductPrice);

    Map<String, String> deleteProductPriceStore(Long id);

    List<StoreProductPrice> getProductPricesForPeriod(Long id, String dateStart, String dateEnd);

    List<Map<String, String>> getCurrentStoreProductPrices(Long productId, Long firstStoreId, Long secondStoreId);

    List<Map<String, String>> getAllStoresProductPrices(Long productId);

    List<Map<String, String>> getProductPrices(Long id, int offset, int pageSize);

    List<Map<String, String>> getProductPricesOneStore(Long productId, Long storeId, int offset, int pageSize);

    Map<String, String> uploadFilePrices(MultipartFile file);

}
