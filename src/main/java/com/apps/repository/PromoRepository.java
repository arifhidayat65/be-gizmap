package com.apps.repository;

import com.apps.model.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromoRepository extends JpaRepository<Promo, Long> {
    Optional<Promo> findByCode(String code);
    
    List<Promo> findByIsActiveAndStartDateBeforeAndEndDateAfterAndCurrentUsesLessThan(
        Boolean isActive,
        LocalDateTime currentDate,
        LocalDateTime currentDate2,
        Integer maxUses
    );

    Optional<Promo> findByCodeAndIsActiveAndStartDateBeforeAndEndDateAfterAndCurrentUsesLessThan(
        String code,
        Boolean isActive,
        LocalDateTime currentDate,
        LocalDateTime currentDate2,
        Integer maxUses
    );
}
