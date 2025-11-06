package com.example.restaurant.controller.customer;

import com.example.restaurant.domain.customer.Customer;
import com.example.restaurant.domain.customer.PointHistory;
import com.example.restaurant.dto.customer.CustomerResponse;
import com.example.restaurant.repository.customer.CustomerRepository;
import com.example.restaurant.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository customerRepo;

    @GetMapping
    public ResponseEntity<List<Customer>> getAll() {
        return ResponseEntity.ok(customerRepo.findAll());
    }

    @GetMapping("/{phone}")
    public ResponseEntity<CustomerResponse> getByPhone(@PathVariable String phone) {
        return ResponseEntity.ok(customerService.getByPhone(phone));
    }

    @GetMapping("/{id}/histories")
    public ResponseEntity<List<PointHistory>> getHistories(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getHistories(id));
    }
}
