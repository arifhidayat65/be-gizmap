package com.apps.repository;

import com.apps.model.Discount;
import com.apps.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByProduct(Product product);
    List<Discount> findByIsActiveAndStartDateBeforeAndEndDateAfter(
        Boolean isActive, 
        LocalDateTime currentDate, 
        LocalDateTime currentDate2
    );
    List<Discount> findByProductAndIsActiveAndStartDateBeforeAndEndDateAfter(
        Product product,
        Boolean isActive,
        LocalDateTime currentDate,
        LocalDateTime currentDate2
    );
}
