package com.ilyaevteev.productmonitoring.repository;

import com.ilyaevteev.productmonitoring.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
}
