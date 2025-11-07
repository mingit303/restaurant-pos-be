package com.example.restaurant.controller.customer;

import com.example.restaurant.domain.customer.Customer;
import com.example.restaurant.domain.customer.PointHistory;
import com.example.restaurant.dto.customer.CustomerResponse;
import com.example.restaurant.repository.customer.CustomerRepository;
import com.example.restaurant.service.customer.CustomerService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<Page<Customer>> searchCustomers(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) Integer minPoints,
            @RequestParam(defaultValue = "totalPoints,desc") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        String sortField = sort.split(",")[0];
        Sort.Direction direction = sort.endsWith(",asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));

        Page<Customer> result = customerRepo.search(keyword, minPoints, pageable);
        return ResponseEntity.ok(result);
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
