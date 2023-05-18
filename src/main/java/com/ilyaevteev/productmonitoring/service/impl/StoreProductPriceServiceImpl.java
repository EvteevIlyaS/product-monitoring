package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.exception.exceptionlist.BadRequestException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.FailedDependencyException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.UnsupportedMediaTypeException;
import com.ilyaevteev.productmonitoring.util.CSVHelper;
import com.ilyaevteev.productmonitoring.model.StoreProductPrice;
import com.ilyaevteev.productmonitoring.repository.StoreProductPricesRepository;
import com.ilyaevteev.productmonitoring.service.ProductService;
import com.ilyaevteev.productmonitoring.service.StoreProductPriceService;
import com.ilyaevteev.productmonitoring.service.StoreService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreProductPriceServiceImpl implements StoreProductPriceService {
    private final StoreProductPricesRepository storeProductPricesRepository;

    private final StoreService storeService;
    private final ProductService productService;

    @Override
    public void addStoreProductPrice(StoreProductPrice storeProductPrice) {
        try {
            storeProductPricesRepository.save(storeProductPrice);
        } catch (Exception e) {
            String message = "Wrong store product price";
            log.error(message);
            throw new BadRequestException(message);
        }
    }

    @Override
    public List<StoreProductPrice> getProductPricesForPeriod(Long id, String dateStart, String dateEnd) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        List<StoreProductPrice> storeProductPrices;

        try {
            storeProductPrices = storeProductPricesRepository.findAllByProductIdAndDateBetweenOrderByDate(id, format.parse(dateStart), format.parse(dateEnd));
        } catch (Exception e) {
            String message = "Wrong store product price";
            log.error(message);
            throw new BadRequestException(message);
        }

        return storeProductPrices;
    }

    @Override
    @Transactional
    public Map<String, Long> getCurrentStoreProductPrices(Long productId, Long firstStoreId, Long secondStoreId) {
        Map<String, Long> storeProductPrice = new HashMap<>();

        StoreProductPrice currentFirstStoreProductPrice = storeProductPricesRepository.getFirstByProductIdAndStoreIdOrderByDateDesc(productId, firstStoreId);
        StoreProductPrice currentSecondStoreProductPrice = storeProductPricesRepository.getFirstByProductIdAndStoreIdOrderByDateDesc(productId, secondStoreId);

        if (currentFirstStoreProductPrice == null | currentSecondStoreProductPrice == null) {
            String message = "Products not found";
            log.error(message);
            throw new BadRequestException(message);
        }

        storeProductPrice.put(storeService.getStoreById(firstStoreId).getName(), currentFirstStoreProductPrice.getPrice());
        storeProductPrice.put(storeService.getStoreById(secondStoreId).getName(), currentSecondStoreProductPrice.getPrice());

        return storeProductPrice;
    }

    @Override
    @Transactional
    public Map<String, Long> getAllStoresProductPrices(Long productId) {
        Map<String, Long> storeProductPrice = new HashMap<>();
        List<Long> storeIds = storeService.getAllStoreIds();

        storeIds.forEach(storeId -> {
            StoreProductPrice currentStoreProductPrice = storeProductPricesRepository.getFirstByProductIdAndStoreIdOrderByDateDesc(productId, storeId);
            if (currentStoreProductPrice != null) {
                storeProductPrice.put(storeService.getStoreById(storeId).getName(), currentStoreProductPrice.getPrice());
            }
        });

        return storeProductPrice;
    }

    @Override
    public Map<Date, Long> getProductPrices(Long id, int offset, int pageSize) {
        Pageable page = PageRequest.of(offset, pageSize);
        Map<Date, Long> productPrices = new TreeMap<>();

        List<StoreProductPrice> storeProductPrices = storeProductPricesRepository.findAllByProductIdOrderByDate(id, page);
        storeProductPrices.forEach(el -> productPrices.put(el.getDate(), el.getPrice()));

        return productPrices;
    }

    @Override
    public Map<Date, Long> getProductPricesOneStore(Long productId, Long storeId, int offset, int pageSize) {
        Pageable page = PageRequest.of(offset, pageSize);
        Map<Date, Long> productPricesOneStore = new TreeMap<>();

        List<StoreProductPrice> storeProductPrices = storeProductPricesRepository.findAllByProductIdAndStoreIdOrderByDate(productId, storeId, page);
        storeProductPrices.forEach(el -> productPricesOneStore.put(el.getDate(), el.getPrice()));

        return productPricesOneStore;
    }

    @Override
    @Transactional
    public void uploadFilePrices(MultipartFile file) {
        List<StoreProductPrice> prices;

        if (CSVHelper.hasCSVFormat(file)) {
            prices = new ArrayList<>();
            try {
                List<Map<String, String>> rawPricesData = CSVHelper.csvToEntities(file.getInputStream(), CSVHelper.PRODUCT_PRICE_HEADERS);
                rawPricesData.forEach(p -> {
                    StoreProductPrice price = new StoreProductPrice();
                    price.setStore(storeService.getStoreById(Long.parseLong(p.get("storeId"))));
                    price.setProduct(productService.getProductById(Long.parseLong(p.get("productId"))));
                    price.setPrice(Long.parseLong(p.get("price")));
                    price.setDate(new Date());
                    prices.add(price);
                });

                storeProductPricesRepository.saveAll(prices);

            } catch (Exception e) {
                String message = "Fail to store csv data";
                log.error(message);
                if (e.getMessage().contains("No products found by id") | e.getMessage().contains("No stores found by id")) {
                    throw new FailedDependencyException(message);
                }
                throw new BadRequestException(message);
            }

        } else {
            String message = "Wrong data format";
            log.error(message);
            throw new UnsupportedMediaTypeException(message);
        }
    }

}
