// repository/order/OrderItemRepository.java
package com.example.restaurant.repository.order;
import com.example.restaurant.domain.order.OrderItem;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("""
        SELECT new map(
            i.menuItem.name as name,
            SUM(i.quantity) as quantitySold,
            SUM(i.lineTotal) as totalRevenue
        )
        FROM OrderItem i
        WHERE FUNCTION('MONTH', i.order.createdAt) = :month
          AND FUNCTION('YEAR', i.order.createdAt) = :year
        GROUP BY i.menuItem.name
        ORDER BY SUM(i.quantity) DESC
    """)
    List<Map<String, Object>> findTopMenuItems(@Param("month") Integer month, @Param("year") Integer year);

    @Query("""
        SELECT COALESCE(SUM(i.quantity), 0)
        FROM OrderItem i
        WHERE DATE(i.order.createdAt) = CURRENT_DATE
    """)
    Integer countItemsSoldToday();

    @Query("""
        SELECT new map(
            m.name as name,
            SUM(oi.quantity) as quantitySold,
            SUM(oi.lineTotal) as totalRevenue
        )
        FROM OrderItem oi
        JOIN oi.menuItem m
        WHERE FUNCTION('MONTH', oi.createdAt) = :month
        AND FUNCTION('YEAR', oi.createdAt) = :year
        GROUP BY m.name
        ORDER BY SUM(oi.quantity) DESC
    """)
    List<Map<String, Object>> findTopSelling(@Param("month") Integer month, @Param("year") Integer year);

    @Query("""
        SELECT new map(
            m.name as name,
            SUM(oi.quantity) as quantitySold,
            SUM(oi.lineTotal) as totalRevenue
        )
        FROM OrderItem oi
        JOIN oi.menuItem m
        WHERE FUNCTION('MONTH', oi.createdAt) = :month
        AND FUNCTION('YEAR', oi.createdAt) = :year
        GROUP BY m.name
        ORDER BY SUM(oi.quantity) ASC
    """)
    List<Map<String, Object>> findLeastSelling(@Param("month") Integer month, @Param("year") Integer year);

    boolean existsByMenuItem_Id(Long id);

    List<OrderItem> findTop10ByMenuItem_Id(Long id);
    boolean existsByChef_Id(Long chefId);

}
