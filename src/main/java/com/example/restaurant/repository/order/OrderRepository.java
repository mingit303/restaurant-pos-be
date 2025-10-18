// repository/order/OrderRepository.java
package com.example.restaurant.repository.order;

import com.example.restaurant.domain.order.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    boolean existsByTableIdAndStatusIn(Long tableId, List<OrderStatus> statuses);
    Optional<Order> findFirstByTableIdAndStatusIn(Long tableId, List<OrderStatus> statuses);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select o from Order o where o.id=:id")
    Optional<Order> findByIdForUpdate(@Param("id") Long id);
}
