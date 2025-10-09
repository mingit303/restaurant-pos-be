// package com.example.restaurant.controller;                    // 1

// import com.example.restaurant.dto.reservation.*;              // 2
// import com.example.restaurant.service.*;                      // 3
// import jakarta.validation.Valid;                              // 4
// import org.springframework.security.access.prepost.PreAuthorize; // 5
// import org.springframework.web.bind.annotation.*;             // 6

// @RestController                                                // 7
// @RequestMapping("/reservations")                               // 8
// public class ReservationController {

//     private final ReservationService reservationService;       // 9

//     public ReservationController(ReservationService reservationService) { // 10
//         this.reservationService = reservationService;
//     }

//     @PostMapping                                               // 11: POST /reservations
//     @PreAuthorize("hasAnyAuthority('ROLE_CASHIER','ROLE_ADMIN')") // 12: cashier/admin tạo reservation
//     public ReservationResponse create(@Valid @RequestBody ReservationRequest body) { // 13
//         return reservationService.create(body);                // 14
//     }

//     @PatchMapping("/{id}/confirm")                             // 15
//     @PreAuthorize("hasAuthority('ROLE_ADMIN')")                // 16: chỉ admin confirm
//     public ReservationResponse confirm(@PathVariable Long id) { // 17
//         return reservationService.confirm(id);                 // 18
//     }

//     @PatchMapping("/{id}/arrive")                              // 19
//     @PreAuthorize("hasAnyAuthority('ROLE_WAITER','ROLE_ADMIN')") // 20: waiter/admin đánh dấu tới
//     public ReservationResponse arrive(@PathVariable Long id) { // 21
//         return reservationService.arrive(id);                  // 22
//     }

//     @PatchMapping("/{id}/cancel")                              // 23
//     @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_CASHIER')") // 24: admin/cashier hủy
//     public ReservationResponse cancel(@PathVariable Long id) { // 25
//         return reservationService.cancel(id);                  // 26
//     }
// }
