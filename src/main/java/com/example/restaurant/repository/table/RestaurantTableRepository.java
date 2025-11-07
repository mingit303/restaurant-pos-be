package com.example.restaurant.repository.table;           
import com.example.restaurant.domain.table.RestaurantTable;
import com.example.restaurant.domain.table.TableStatus;

import jakarta.persistence.LockModeType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;         
import org.springframework.data.repository.query.Param;

import java.util.*;                                        

public interface RestaurantTableRepository
        extends JpaRepository<RestaurantTable, Long> {     

    Optional<RestaurantTable> findByCode(String code);
    boolean existsByCode(String code);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from RestaurantTable t where t.id=:id")
    Optional<RestaurantTable> findByIdForUpdate(@Param("id") Long id);
    Page<RestaurantTable> findByCodeContainingIgnoreCase(String code, Pageable pageable);

    Page<RestaurantTable> findByCapacityGreaterThanEqual(int capacity, Pageable pageable);

    Page<RestaurantTable> findByStatus(TableStatus status, Pageable pageable);
    Page<RestaurantTable> findByStatusAndCodeContainingIgnoreCase(TableStatus status, String code, Pageable pageable);
    Page<RestaurantTable> findByStatusAndCapacityGreaterThanEqual(TableStatus status, int capacity, Pageable pageable);

}
