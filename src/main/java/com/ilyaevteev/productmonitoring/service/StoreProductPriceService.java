package com.ilyaevteev.productmonitoring.service;

import com.ilyaevteev.productmonitoring.model.StoreProductPrice;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface StoreProductPriceService {
    void addStoreProductPrice(StoreProductPrice storeProductPrice);

    List<StoreProductPrice> getProductPricesForPeriod(Long id, String dateStart, String dateEnd);

    Map<String, Long> getCurrentStoreProductPrices(Long productId, Long firstStoreId, Long secondStoreId);

    Map<String, Long> getAllStoresProductPrices(Long productId);

    Map<Date, Long> getProductPrices(Long id, int offset, int pageSize);

    Map<Date, Long> getProductPricesOneStore(Long productId, Long storeId, int offset, int pageSize);

    void uploadFilePrices(MultipartFile file);

}
