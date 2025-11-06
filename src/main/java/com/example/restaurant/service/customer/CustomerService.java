package com.example.restaurant.service.customer;

import com.example.restaurant.domain.customer.Customer;
import com.example.restaurant.domain.customer.PointHistory;
import com.example.restaurant.dto.customer.CustomerResponse;
import com.example.restaurant.repository.customer.CustomerRepository;
import com.example.restaurant.repository.customer.PointHistoryRepository;
import com.example.restaurant.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Propagation;
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepo;
    private final PointHistoryRepository historyRepo;

    @Transactional
    public Customer getOrCreateCustomer(String phone, String name) {
        System.out.println("👤 [CustomerService] getOrCreateCustomer: phone=" + phone + ", name=" + name);
        return customerRepo.findByPhone(phone)
                .orElseGet(() -> {
                    Customer c = Customer.builder()
                            .phone(phone)
                            .name((name != null && !name.isBlank()) ? name : "Khách " + phone.substring(phone.length()-3))
                            .totalPoints(0)
                            .createdAt(LocalDateTime.now())
                            .build();
                    return customerRepo.save(c);
                });
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void addPoints(Customer c, int points, String desc) {
        c.setTotalPoints(c.getTotalPoints() + points);
        customerRepo.save(c);
        customerRepo.flush();
        historyRepo.save(PointHistory.builder()
                .customer(c)
                .changeAmount(points)
                .description(desc)
                .build());
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void redeemPoints(Customer c, int points, String desc) {
        System.out.println("🔥 [RedeemPoints] Start for customerId=" + c.getId() + ", currentPoints=" + c.getTotalPoints());
        if (c.getTotalPoints() < points)
            throw new RuntimeException("Không đủ điểm để sử dụng.");
        c.setTotalPoints(c.getTotalPoints() - points);
        customerRepo.save(c);
        customerRepo.flush();
        historyRepo.save(PointHistory.builder()
                .customer(c)
                .changeAmount(-points)
                .description(desc)
                .build());
    }

    public List<PointHistory> getHistories(Long customerId) {
        return historyRepo.findByCustomerIdOrderByCreatedAtDesc(customerId);
    }

    public CustomerResponse getByPhone(String phone) {
        Customer c = customerRepo.findByPhone(phone)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));
        return new CustomerResponse(
            c.getId(), c.getName(), c.getPhone(), c.getTotalPoints(), c.getCreatedAt()
        );
    }

    public Customer getById(Long id) {
        return customerRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy khách hàng"));
    }
}
