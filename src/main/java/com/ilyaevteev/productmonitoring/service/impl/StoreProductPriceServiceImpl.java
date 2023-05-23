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
import com.ilyaevteev.productmonitoring.util.ExcelHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
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
    public Map<String, String> addStoreProductPrice(StoreProductPrice storeProductPrice) {
        try {
            storeProductPrice.setDate(new Date());
            storeProductPricesRepository.save(storeProductPrice);
            return Map.of("id", storeProductPrice.getId().toString(),
                    "price", storeProductPrice.getPrice().toString());
        } catch (Exception e) {
            String message = "Wrong store product price";
            log.error(message);
            throw new BadRequestException(message);
        }
    }

    @Override
    public Map<String, String> deleteProductPriceStore(Long id) {
        storeProductPricesRepository.deleteById(id);
        return Map.of("id", id.toString());
    }

    @Override
    public List<StoreProductPrice> getProductPricesForPeriod(Long id, String dateStart, String dateEnd) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            return storeProductPricesRepository.findAllByProductIdAndDateBetweenOrderByDate(id, format.parse(dateStart), format.parse(dateEnd));
        } catch (Exception e) {
            String message = "Wrong store product price";
            log.error(message);
            throw new BadRequestException(message);
        }

    }

    @Override
    @Transactional
    public List<Map<String, String>> getCurrentStoreProductPrices(Long productId, Long firstStoreId, Long secondStoreId) {
        List<Map<String, String>> storeProductPrice;

        StoreProductPrice currentFirstStoreProductPrice = storeProductPricesRepository.getFirstByProductIdAndStoreIdOrderByDateDesc(productId, firstStoreId);
        StoreProductPrice currentSecondStoreProductPrice = storeProductPricesRepository.getFirstByProductIdAndStoreIdOrderByDateDesc(productId, secondStoreId);

        if (currentFirstStoreProductPrice == null | currentSecondStoreProductPrice == null) {
            String message = "Products not found";
            log.error(message);
            throw new BadRequestException(message);
        }

        storeProductPrice = Arrays.asList(
                Map.of("store", storeService.getStoreById(firstStoreId).getName(),
                        "price", currentFirstStoreProductPrice.getPrice().toString()),
                Map.of("store", storeService.getStoreById(secondStoreId).getName(),
                        "price", currentSecondStoreProductPrice.getPrice().toString())
        );

        return storeProductPrice;
    }

    @Override
    @Transactional
    public List<Map<String, String>> getAllStoresProductPrices(Long productId) {
        List<Map<String, String>> storeProductPrice = new ArrayList<>();
        List<Long> storeIds = storeService.getAllStoreIds();

        storeIds.forEach(storeId -> {
            StoreProductPrice currentStoreProductPrice = storeProductPricesRepository.getFirstByProductIdAndStoreIdOrderByDateDesc(productId, storeId);
            if (currentStoreProductPrice != null) {
                storeProductPrice.add(Map.of("store", storeService.getStoreById(storeId).getName(), "price", currentStoreProductPrice.getPrice().toString()));
            }
        });

        return storeProductPrice;
    }

    @Override
    public List<Map<String, String>> getProductPrices(Long id, int offset, int pageSize) {
        Pageable page = PageRequest.of(offset, pageSize);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        List<Map<String, String>> productPrices = new ArrayList<>();

        List<StoreProductPrice> storeProductPrices = StreamEx.of(storeProductPricesRepository.findAllByProductIdOrderByDate(id, page))
                .distinct(el -> format.format(el.getDate())).toList();
        storeProductPrices.forEach(el -> productPrices.add(Map.of("date", el.getDate().toString(), "price", el.getPrice().toString())));

        return productPrices;
    }

    @Override
    public List<Map<String, String>> getProductPricesOneStore(Long productId, Long storeId, int offset, int pageSize) {
        Pageable page = PageRequest.of(offset, pageSize);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        List<Map<String, String>> productPricesOneStore = new ArrayList<>();

        List<StoreProductPrice> storeProductPrices = StreamEx.of(storeProductPricesRepository.findAllByProductIdAndStoreIdOrderByDate(productId, storeId, page))
                .distinct(el -> format.format(el.getDate())).toList();
        storeProductPrices.forEach(el -> productPricesOneStore.add(Map.of("date", el.getDate().toString(), "price", el.getPrice().toString())));

        return productPricesOneStore;
    }

    @Override
    @Transactional
    public Map<String, String> uploadFilePrices(MultipartFile file) {
        Boolean isCSV = CSVHelper.hasCSVFormat(file);
        Boolean isExcel = ExcelHelper.hasExcelFormat(file);

        if (isCSV | isExcel) {
            List<StoreProductPrice> prices = new ArrayList<>();
            try {
                List<Map<String, String>> rawPricesData = isCSV ?
                        CSVHelper.csvToEntities(file.getInputStream(), CSVHelper.PRODUCT_PRICE_HEADERS) :
                        ExcelHelper.excelToEntities(file.getInputStream(), ExcelHelper.PRODUCT_PRICE_HEADERS);
                rawPricesData.forEach(p -> {
                    StoreProductPrice price = new StoreProductPrice();
                    price.setStore(storeService.getStoreById(Long.parseLong(p.get("storeId"))));
                    price.setProduct(productService.getProductById(Long.parseLong(p.get("productId"))));
                    price.setPrice(Long.parseLong(p.get("price")));
                    price.setDate(new Date());
                    prices.add(price);
                });

                storeProductPricesRepository.saveAll(prices);
                return Map.of("file", Objects.requireNonNull(file.getOriginalFilename()));

            } catch (Exception e) {
                String message = "Fail to store data";
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
