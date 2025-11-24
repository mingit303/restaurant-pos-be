package com.example.restaurant.repository.customer;

import com.example.restaurant.domain.customer.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    List<PointHistory> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    boolean existsByCustomer_Id(Long id);

}
