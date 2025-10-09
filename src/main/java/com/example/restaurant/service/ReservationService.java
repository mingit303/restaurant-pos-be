// package com.example.restaurant.service;                               // 1

// import com.example.restaurant.domain.table.*;                         // 2: RestaurantTable, TableStatus
// import com.example.restaurant.domain.user.User;
// import com.example.restaurant.domain.reservation.*;                   // 3: Reservation, ReservationStatus
// import com.example.restaurant.dto.reservation.*;                      // 4: DTOs
// import com.example.restaurant.exception.*;                            // 5: NotFound/Conflict/BadRequest
// import com.example.restaurant.repository.table.*;                     // 6
// import com.example.restaurant.repository.user.UserRepository;
// import com.example.restaurant.repository.reservation.*;               // 7

// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.stereotype.Service;                        // 8
// import org.springframework.transaction.annotation.Transactional;      // 9
// import java.time.*;                                                  // 10
// import java.util.*;                                                  // 11

// @Service                                                              // 12
// public class ReservationService {
//     private final RestaurantTableRepository tableRepo;                // 13
//     private final ReservationRepository reservationRepo;              // 14
//     private final UserRepository userRepository;

//     public ReservationService(RestaurantTableRepository tableRepo,
//                               ReservationRepository reservationRepo,
//                               UserRepository userRepository) { // 15
//         this.tableRepo = tableRepo;
//         this.reservationRepo = reservationRepo;
//         this.userRepository = userRepository;
//     }

//     // 16: Danh sách trạng thái đang hoạt động để check overlap
//     private static List<ReservationStatus> activeStatuses() {
//         return List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED, ReservationStatus.ARRIVED);
//     }

//     // 17: Tự expire các reservation quá hạn 15' (PENDING/CONFIRMED)
//     @Transactional
//     public int expireOverdueReservations() {
//         LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);               // 18: mốc hiện tại - 15'
//         List<Reservation> list = reservationRepo.findExpiredCandidates(threshold);    // 19: lấy candidates
//         int changed = 0;                                                              // 20: đếm số record đổi trạng thái
//         for (Reservation r : list) {                                                  // 21
//             if (r.getStatus() == ReservationStatus.PENDING || r.getStatus() == ReservationStatus.CONFIRMED) {
//                 r.setStatus(ReservationStatus.EXPIRED);                               // 22: chuyển EXPIRED
//                 RestaurantTable t = r.getTable();                                     // 23
//                 if (t.getStatus() == TableStatus.RESERVED) {                          // 24: nếu bàn đang RESERVED…
//                     t.setStatus(TableStatus.FREE);                                    // 25: …trả về FREE
//                 }
//                 changed++;                                                            // 26
//             }
//         }
//         return changed;                                                                // 27
//     }

//     // 28: Check không trùng giờ (có buffer 30' trước/sau)
//     private void assertNoConflict(Long tableId, LocalDateTime start, LocalDateTime end) {
//         LocalDateTime windowStart = start.minusMinutes(30);                            // 29: buffer trước 30'
//         LocalDateTime windowEnd   = end.plusMinutes(30);                               // 30: buffer sau 30'
//         var overlaps = reservationRepo.findOverlaps(tableId, activeStatuses(), windowStart, windowEnd); // 31
//         if (!overlaps.isEmpty()) {                                                     // 32
//             throw new ConflictException("Bàn đang bận trong khoảng thời gian (đã tính buffer 30').");
//         }
//     }

//     // 33: Tạo reservation (Cashier/Admin)
//     @Transactional
//     public ReservationResponse create(ReservationRequest req) {
//         expireOverdueReservations();                                                   // 34: dọn reservation quá hạn trước khi tạo
//         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
//         User creator = null;
//         if (auth != null) {
//             String username = auth.getName();
//             creator = userRepository.findByUsername(username).orElse(null);
//         }
        
//         RestaurantTable table = tableRepo.findById(req.tableId())                      // 35
//                 .orElseThrow(() -> new NotFoundException("Không tìm thấy bàn."));

//         if (req.partySize() > table.getCapacity()) {                                   // 36
//             throw new BadRequestException("Số khách vượt quá sức chứa bàn.");
//         }

//         LocalDateTime start = req.startTime();                                         // 37
//         LocalDateTime end   = start.plusMinutes(req.durationMinutes());                // 38

//         assertNoConflict(table.getId(), start, end);                                   // 39

//         Reservation r = Reservation.builder()                                          // 40
//                 .table(table)
//                 .customerName(req.customerName())
//                 .customerPhone(req.customerPhone())
//                 .startTime(start)
//                 .endTime(end)
//                 .partySize(req.partySize())
//                 .status(ReservationStatus.PENDING)// 41: ban đầu PENDING
//                 .createdBy(creator)                                     
//                 .build();

//         reservationRepo.save(r);                                                       // 42
//         table.setStatus(TableStatus.RESERVED);                                         // 43: giữ bàn

//         return map(r);                                                                 // 44
//     }

//     // 45: Confirm (Admin)
//     @Transactional
//     public ReservationResponse confirm(Long id) {
//         expireOverdueReservations();                                                   // 46
//         Reservation r = reservationRepo.findById(id)                                   // 47
//                 .orElseThrow(() -> new NotFoundException("Không tìm thấy reservation."));
//         if (r.getStatus() != ReservationStatus.PENDING) {                              // 48
//             throw new BadRequestException("Chỉ reservation PENDING mới được confirm.");
//         }
//         assertNoConflict(r.getTable().getId(), r.getStartTime(), r.getEndTime());      // 49
//         r.setStatus(ReservationStatus.CONFIRMED);                                      // 50
//         r.getTable().setStatus(TableStatus.RESERVED);                                  // 51
//         return map(r);                                                                 // 52
//     }

//     // 53: Arrive (Waiter/Admin)
//     @Transactional
//     public ReservationResponse arrive(Long id) {
//         expireOverdueReservations();                                                   // 54
//         Reservation r = reservationRepo.findById(id)                                   // 55
//                 .orElseThrow(() -> new NotFoundException("Không tìm thấy reservation."));
//         if (r.getStatus() != ReservationStatus.CONFIRMED && r.getStatus() != ReservationStatus.PENDING) {
//             throw new BadRequestException("Chỉ PENDING/CONFIRMED mới chuyển ARRIVED."); // 56
//         }
//         r.setStatus(ReservationStatus.ARRIVED);                                        // 57
//         r.getTable().setStatus(TableStatus.OCCUPIED);                                  // 58
//         return map(r);                                                                 // 59
//     }

//     // 60: Cancel (Admin/Cashier)
//     @Transactional
//     public ReservationResponse cancel(Long id) {
//         expireOverdueReservations();                                                   // 61
//         Reservation r = reservationRepo.findById(id)                                   // 62
//                 .orElseThrow(() -> new NotFoundException("Không tìm thấy reservation."));
//         if (r.getStatus() == ReservationStatus.CANCELED || r.getStatus() == ReservationStatus.EXPIRED) {
//             throw new BadRequestException("Reservation đã không còn hiệu lực.");       // 63
//         }
//         r.setStatus(ReservationStatus.CANCELED);                                       // 64
//         if (r.getTable().getStatus() == TableStatus.RESERVED) {                        // 65
//             r.getTable().setStatus(TableStatus.FREE);                                  // 66
//         }
//         return map(r);                                                                 // 67
//     }

//     // 68: Map Entity → DTO Response
//     private ReservationResponse map(Reservation r) {
//         return new ReservationResponse(
//                 r.getId(),
//                 r.getTable().getId(),
//                 r.getTable().getCode(),
//                 r.getCustomerName(),
//                 r.getCustomerPhone(),
//                 r.getStartTime(),
//                 r.getEndTime(),
//                 r.getPartySize(),
//                 r.getStatus(),
//                 r.getCreatedBy()!=null ? r.getCreatedBy().getId() : null,
//                 r.getCreatedBy()!=null ? r.getCreatedBy().getUsername() : null,
//                 r.getCreatedAt()
//         );
//     }
// }
