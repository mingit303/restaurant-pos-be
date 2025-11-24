package com.example.restaurant.repository.invoice;

import com.example.restaurant.domain.invoice.Invoice;
import com.example.restaurant.domain.invoice.InvoiceStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByTransactionRef(String ref);

    List<Invoice> findByStatusAndPaidAtBetween(
        InvoiceStatus status,
        LocalDateTime from,
        LocalDateTime to
    );

    // Thống kê theo tháng
    @Query("""
        SELECT i FROM Invoice i 
        WHERE i.status = :status 
          AND FUNCTION('MONTH', i.paidAt) = :month 
          AND FUNCTION('YEAR', i.paidAt) = :year
    """)
    List<Invoice> findByMonthAndYear(
        @Param("status") InvoiceStatus status,
        @Param("month") int month,
        @Param("year") int year
    );

    // Thống kê theo năm
    @Query("""
        SELECT i FROM Invoice i 
        WHERE i.status = :status 
          AND FUNCTION('YEAR', i.paidAt) = :year
    """)
    List<Invoice> findByYear(
        @Param("status") InvoiceStatus status,
        @Param("year") int year
    );
    
    @Query("""
    SELECT SUM(i.total) FROM Invoice i
    WHERE i.status = 'PAID'
        AND i.paidAt BETWEEN :from AND :to
    """)
    BigDecimal sumTotalBetween(@Param("from") LocalDateTime from,
                            @Param("to") LocalDateTime to);

    @Query("""
        SELECT COALESCE(SUM(i.total), 0)
        FROM Invoice i
        WHERE i.status = 'PAID'
        AND DATE(i.paidAt) = CURRENT_DATE
    """)
    BigDecimal sumRevenueToday();
    boolean existsByCashier_Id(Long cashierId);
    boolean existsByCustomer_Id(Long customerId);
    boolean existsByVoucher_Id(Long voucherId);

}
