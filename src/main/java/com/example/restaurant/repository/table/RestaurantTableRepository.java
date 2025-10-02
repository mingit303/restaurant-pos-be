package com.example.restaurant.repository.table;           // 1

import com.example.restaurant.domain.table.RestaurantTable;// 2
import org.springframework.data.jpa.repository.*;          // 3
import java.util.*;                                        // 4

public interface RestaurantTableRepository
        extends JpaRepository<RestaurantTable, Long> {     // 5: có sẵn CRUD

    Optional<RestaurantTable> findByCode(String code);     // 6: nếu cần lookup theo mã bàn
    boolean existsByCode(String code);
}
