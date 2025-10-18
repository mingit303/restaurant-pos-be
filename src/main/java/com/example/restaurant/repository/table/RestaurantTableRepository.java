package com.example.restaurant.repository.table;           // 1

import com.example.restaurant.domain.table.RestaurantTable;// 2

import jakarta.persistence.LockModeType;

import org.springframework.data.jpa.repository.*;          // 3
import org.springframework.data.repository.query.Param;

import java.util.*;                                        // 4

public interface RestaurantTableRepository
        extends JpaRepository<RestaurantTable, Long> {     // 5: có sẵn CRUD

    Optional<RestaurantTable> findByCode(String code);
    boolean existsByCode(String code);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from RestaurantTable t where t.id=:id")
    Optional<RestaurantTable> findByIdForUpdate(@Param("id") Long id);
}
