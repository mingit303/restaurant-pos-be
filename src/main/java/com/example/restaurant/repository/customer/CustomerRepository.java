package com.example.restaurant.repository.customer;

import com.example.restaurant.domain.customer.Customer;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByPhone(String phone);
    @Query("""
        SELECT c FROM Customer c
        WHERE 
          (:keyword = '' OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR c.phone LIKE CONCAT('%', :keyword, '%'))
          AND (:minPoints IS NULL OR c.totalPoints >= :minPoints)
    """)
    Page<Customer> search(@Param("keyword") String keyword,
                          @Param("minPoints") Integer minPoints,
                          Pageable pageable);
}
