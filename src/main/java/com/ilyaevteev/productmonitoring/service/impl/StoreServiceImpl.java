package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.exception.exceptionlist.BadRequestException;
import com.ilyaevteev.productmonitoring.exception.exceptionlist.NotFoundException;
import com.ilyaevteev.productmonitoring.model.Address;
import com.ilyaevteev.productmonitoring.model.Store;
import com.ilyaevteev.productmonitoring.repository.StoreRepository;
import com.ilyaevteev.productmonitoring.service.AddressService;
import com.ilyaevteev.productmonitoring.service.StoreService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;

    private final AddressService addressService;

    @Override
    public Page<Store> getStoresDirectory(Pageable pageable) {
        return storeRepository.findAll(pageable);
    }

    @Override
    public Store getStoreById(Long id) {
        return storeRepository.findById(id).orElseGet(() -> {
            String message = "No stores found";
            log.error(message);
            throw new NotFoundException(message);
        });
    }

    @Override
    public List<Long> getAllStoreIds() {
        return storeRepository.getAllStoreIds();
    }

    @Override
    @Transactional
    public Map<String, String> addStore(String name, String address) {
        try {
            Store store = new Store();
            store.setName(name);
            Address returnedAddress = addressService.addAddress(address);
            store.setAddress(returnedAddress);
            storeRepository.save(store);
            return Map.of("id", store.getId().toString(),
                    "name", store.getName(),
                    "addressCity", returnedAddress.getCity(),
                    "addressStreet", returnedAddress.getStreet(),
                    "addressHouse", returnedAddress.getHouse(),
                    "addressPostcode", returnedAddress.getPostcode());
        } catch (Exception e) {
            String message = "Wrong store data";
            log.error(message);
            throw new BadRequestException(message);
        }
    }
}
