package com.example.restaurant.repository.employee;

import com.example.restaurant.domain.employee.Employee;
import com.example.restaurant.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByUserUsername(String username);
    Optional<Employee> findByUser(User user);

    // Tìm kiếm nhân viên
    @Query("""
        SELECT e FROM Employee e
        LEFT JOIN e.user u
        WHERE
        u.role.name <> 'ROLE_ADMIN' 
          AND (:keyword IS NULL OR LOWER(e.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:gender IS NULL OR e.gender = :gender)
          AND (:role IS NULL OR u.role.name = :role)
    """)
    Page<Employee> searchEmployees(
        @Param("keyword") String keyword,
        @Param("gender") String gender,
        @Param("role") String role,
        Pageable pageable
    );

    /* PHỤC VỤ — đếm số ORDER đã hoàn tất thanh toán (PAID) */
    @Query("""
        SELECT new map(
            e.fullName as name,
            COUNT(o.id) as metric,
            0 as revenue
        )
        FROM Employee e
        JOIN com.example.restaurant.domain.order.Order o ON o.waiter.id = e.id
        WHERE o.status = 'PAID'
          AND FUNCTION('MONTH', o.createdAt) = :month
          AND FUNCTION('YEAR', o.createdAt) = :year
          AND e.user.role.name = 'ROLE_WAITER'
        GROUP BY e.fullName
        ORDER BY COUNT(o.id) DESC
    """)
    List<Map<String, Object>> findWaiterPerformance(
        @Param("month") Integer month,
        @Param("year") Integer year
    );


    /* THU NGÂN — đếm số hóa đơn đã thanh toán + tổng doanh thu */
    @Query("""
        SELECT new map(
            e.fullName as name,
            COUNT(i.id) as metric,
            SUM(i.total) as revenue
        )
        FROM Invoice i
        JOIN i.cashier e
        WHERE i.status = 'PAID'
          AND FUNCTION('MONTH', i.paidAt) = :month
          AND FUNCTION('YEAR', i.paidAt) = :year
        GROUP BY e.fullName
        ORDER BY SUM(i.total) DESC
    """)
    List<Map<String, Object>> findCashierPerformance(
        @Param("month") Integer month,
        @Param("year") Integer year
    );


    /*  BẾP — đếm món đã SERVED hoặc DONE, theo đầu bếp */
    @Query("""
        SELECT new map(
            e.fullName as name,
            COUNT(oi.id) as metric,
            0 as revenue
        )
        FROM OrderItem oi
        JOIN oi.chef e
        WHERE (oi.state = 'DONE' OR oi.state = 'SERVED')
          AND oi.doneAt IS NOT NULL
          AND FUNCTION('MONTH', oi.doneAt) = :month
          AND FUNCTION('YEAR', oi.doneAt) = :year
        GROUP BY e.fullName
        ORDER BY COUNT(oi.id) DESC
    """)
    List<Map<String, Object>> findKitchenPerformance(
        @Param("month") Integer month,
        @Param("year") Integer year
    );
}
