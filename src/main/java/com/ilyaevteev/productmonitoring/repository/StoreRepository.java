package com.ilyaevteev.productmonitoring.repository;

import com.ilyaevteev.productmonitoring.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query("select s.id from Store s")
    List<Long> getAllStoreIds();
}
