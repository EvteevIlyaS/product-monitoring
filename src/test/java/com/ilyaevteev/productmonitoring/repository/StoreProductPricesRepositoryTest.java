package com.ilyaevteev.productmonitoring.repository;

import com.ilyaevteev.productmonitoring.model.StoreProductPrice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class StoreProductPricesRepositoryTest {
    @Autowired
    private StoreProductPricesRepository storeProductPricesRepository;

    @Test
    void findAllByProductIdAndDateBetweenOrderByDate_checkReturnedValue() throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Pageable pageable = PageRequest.of(0, 3);

        Page<StoreProductPrice> storeProductPrices = storeProductPricesRepository.findAllByProductIdAndDateBetweenOrderByDate(
                3L, format.parse("2023-03-20"), format.parse("2023-05-05 13:41:36.130"), pageable);

        assertThat(storeProductPrices.getContent().size()).isEqualTo(3);
    }

    @Test
    void getFirstByProductIdAndStoreIdOrderByDateDesc_checkReturnedValue() {
        StoreProductPrice storeProductPrice = storeProductPricesRepository
                .getFirstByProductIdAndStoreIdOrderByDateDesc(1L, 3L);

        assertThat(storeProductPrice.getPrice()).isEqualTo(190);
    }

    @Test
    void findAllByProductIdOrderByDate_checkReturnedValue() {
        List<StoreProductPrice> storeProductPrices = storeProductPricesRepository
                .findAllByProductIdOrderByDate(1L);

        assertThat(storeProductPrices.size()).isEqualTo(8);
    }

    @Test
    void findAllByProductIdAndStoreIdOrderByDate_checkReturnedValue() {
        List<StoreProductPrice> storeProductPrices = storeProductPricesRepository
                .findAllByProductIdAndStoreIdOrderByDate(1L, 2L);

        assertThat(storeProductPrices.size()).isEqualTo(5);
    }

    @Test
    void deleteById_checkReturnedValue() {
        String id = "1";

        int rowsNumber = storeProductPricesRepository.deleteById(id);

        assertThat(rowsNumber).isEqualTo(1);
    }
}