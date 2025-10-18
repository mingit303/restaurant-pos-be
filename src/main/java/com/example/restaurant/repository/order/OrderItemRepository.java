// repository/order/OrderItemRepository.java
package com.example.restaurant.repository.order;
import com.example.restaurant.domain.order.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {}
