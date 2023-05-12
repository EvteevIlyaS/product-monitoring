package com.ilyaevteev.productmonitoring.repository;

import com.ilyaevteev.productmonitoring.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
