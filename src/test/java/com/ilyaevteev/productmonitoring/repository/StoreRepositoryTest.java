package com.ilyaevteev.productmonitoring.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class StoreRepositoryTest {
    @Autowired
    private StoreRepository storeRepository;

    @Test
    void getAllStoreId() {
        Comparator<Long> comparator = Long::compareTo;
        List<Long> storeIds = Arrays.asList(1L, 2L, 3L);

        List<Long> storeIdsRes = storeRepository.getAllStoreIds();
        storeIdsRes.sort(comparator);
        storeIds.sort(comparator);

        assertThat(storeIdsRes).isEqualTo(storeIds);
    }
}