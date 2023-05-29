package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.exception.exceptionlist.BadRequestException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.FailedDependencyException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.NotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    @Transactional
    public Map<String, String> addStoreProductPrice(Long price, Long storeId, Long productId) {
        try {
            StoreProductPrice storeProductPrice = new StoreProductPrice();
            storeProductPrice.setPrice(price);
            storeProductPrice.setDate(new Date());
            storeProductPrice.setProduct(productService.getProductById(productId));
            storeProductPrice.setStore(storeService.getStoreById(storeId));
            storeProductPrice = storeProductPricesRepository.save(storeProductPrice);
            return Map.of("id", storeProductPrice.getId().toString(),
                    "price", storeProductPrice.getPrice().toString());
        } catch (Exception e) {
            String message = "Wrong store product price";
            log.error(message);
            if (e.getMessage() != null && (e.getMessage().equals("No products found") | e.getMessage().equals("No stores found"))) {
                throw new FailedDependencyException(message);
            }
            throw new BadRequestException(message);
        }
    }

    @Override
    @Transactional
    public Map<String, String> deleteProductPriceStore(Long id) {
        int rowsNumber = storeProductPricesRepository.deleteById(id.toString());
        if (rowsNumber == 0) {
            String message = "No prices found";
            log.error(message);
            throw new NotFoundException(message);
        }
        return Map.of("id", id.toString());
    }

    @Override
    public Page<StoreProductPrice> getProductPricesForPeriod(Long id, String dateStart, String dateEnd, Pageable pageable) {
        try {
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            return storeProductPricesRepository.findAllByProductIdAndDateBetweenOrderByDate(id, format.parse(dateStart), format.parse(dateEnd), pageable);
        } catch (Exception e) {
            String message = "Wrong store product price";
            log.error(message);
            throw new BadRequestException(message);
        }

    }

    @Override
    @Transactional
    public Map<String, Map<String, String>> getCurrentStoreProductPrices(Long productId, Long firstStoreId, Long secondStoreId) {
        Map<String, Map<String, String>> storeProductPrice;

        StoreProductPrice currentFirstStoreProductPrice = storeProductPricesRepository.getFirstByProductIdAndStoreIdOrderByDateDesc(productId, firstStoreId);
        StoreProductPrice currentSecondStoreProductPrice = storeProductPricesRepository.getFirstByProductIdAndStoreIdOrderByDateDesc(productId, secondStoreId);

        if (currentFirstStoreProductPrice == null | currentSecondStoreProductPrice == null) {
            String message = "No prices found";
            log.error(message);
            throw new NotFoundException(message);
        }

        storeProductPrice = Map.of("firstStore",
                Map.of("store", storeService.getStoreById(firstStoreId).getName(),
                        "price", currentFirstStoreProductPrice.getPrice().toString()),
                "secondStore",
                Map.of("store", storeService.getStoreById(secondStoreId).getName(),
                        "price", currentSecondStoreProductPrice.getPrice().toString())
        );

        return storeProductPrice;
    }

    @Override
    @Transactional
    public Page<Map<String, String>> getAllStoresProductPrices(Long productId, Pageable pageable) {
        List<Map<String, String>> storeProductPrice = new ArrayList<>();
        List<Long> storeIds = storeService.getAllStoreIds();

        storeIds.forEach(storeId -> {
            StoreProductPrice currentStoreProductPrice = storeProductPricesRepository.getFirstByProductIdAndStoreIdOrderByDateDesc(productId, storeId);
            if (currentStoreProductPrice != null) {
                storeProductPrice.add(Map.of("store", storeService.getStoreById(storeId).getName(), "price", currentStoreProductPrice.getPrice().toString()));
            }
        });

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), storeProductPrice.size());

        return new PageImpl<>(storeProductPrice.subList(start, end), pageable, storeProductPrice.size());
    }

    @Override
    public Page<Map<String, String>> getProductPrices(Long id, Pageable pageable) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        List<Map<String, String>> productPrices = new ArrayList<>();

        List<StoreProductPrice> storeProductPrices = StreamEx.of(storeProductPricesRepository.findAllByProductIdOrderByDate(id, pageable))
                .distinct(el -> format.format(el.getDate())).toList();
        storeProductPrices.forEach(el -> productPrices.add(Map.of("date", el.getDate().toString(), "price", el.getPrice().toString())));

        return new PageImpl<>(productPrices, pageable, productPrices.size());
    }

    @Override
    public Page<Map<String, String>> getProductPricesOneStore(Long productId, Long storeId, Pageable pageable) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        List<Map<String, String>> productPricesOneStore = new ArrayList<>();

        List<StoreProductPrice> storeProductPrices = StreamEx.of(storeProductPricesRepository.findAllByProductIdAndStoreIdOrderByDate(productId, storeId, pageable))
                .distinct(el -> format.format(el.getDate())).toList();
        storeProductPrices.forEach(el -> productPricesOneStore.add(Map.of("date", el.getDate().toString(), "price", el.getPrice().toString())));

        return new PageImpl<>(productPricesOneStore, pageable, productPricesOneStore.size());
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
                if (e.getMessage() != null && (e.getMessage().equals("No products found") | e.getMessage().equals("No stores found"))) {
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
