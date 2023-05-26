package com.ilyaevteev.productmonitoring.repository;

import com.ilyaevteev.productmonitoring.model.StoreProductPrice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface StoreProductPricesRepository extends JpaRepository<StoreProductPrice, Long> {
    Page<StoreProductPrice> findAllByProductIdAndDateBetweenOrderByDate(Long id, Date dateStart, Date dateEnd, Pageable pageable);

    StoreProductPrice getFirstByProductIdAndStoreIdOrderByDateDesc(Long productId, Long storeId);

    List<StoreProductPrice> findAllByProductIdOrderByDate(Long id, Pageable page);

    List<StoreProductPrice> findAllByProductIdAndStoreIdOrderByDate(Long productId, Long storeId, Pageable page);

    @Modifying
    @Query("delete from StoreProductPrice storeProductPrice where storeProductPrice.id = :id")
    int deleteById(@Param("id") String id);

}
