package com.example.restaurant.service.table;

import com.example.restaurant.domain.table.*;
// import com.example.restaurant.dto.table.*;
import com.example.restaurant.dto.table.request.CreateTableRequest;
import com.example.restaurant.dto.table.request.UpdateTableRequest;
import com.example.restaurant.dto.table.response.TableResponse;
import com.example.restaurant.exception.*;
import com.example.restaurant.repository.order.OrderRepository;
import com.example.restaurant.repository.table.RestaurantTableRepository;
import com.example.restaurant.ws.TableEventPublisher;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableService {

    private final RestaurantTableRepository tableRepo;
    private final TableEventPublisher tableEvents;
    private final OrderRepository orderRepo;

    // Lấy toàn bộ bàn 
    @Transactional(readOnly = true)
    public List<TableResponse> getAll() {
        return tableRepo.findAll().stream()
                .map(t -> new TableResponse(t.getId(), t.getCode(), t.getCapacity(), t.getStatus()))
                .toList();
    }

    //Cập nhật trạng thái bàn (Admin/Waiter/Cashier) 
    @Transactional
    public TableResponse updateStatus(Long id, String status) {
        RestaurantTable table = tableRepo.findByIdForUpdate(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bàn."));

        table.setStatus(TableStatus.valueOf(status));

        TableResponse res = new TableResponse(table.getId(), table.getCode(),
                table.getCapacity(), table.getStatus());

        tableEvents.tableChanged(table.getId(), table.getCode(),
                table.getCapacity(), table.getStatus().name(), "STATUS_CHANGED");

        return res;
    }

    // Tạo bàn mới
    @Transactional
    public TableResponse create(CreateTableRequest req) {
        if (tableRepo.existsByCode(req.getCode())) {
            throw new ConflictException("Mã bàn đã tồn tại.");
        }

        RestaurantTable t = RestaurantTable.builder()
                .code(req.getCode())
                .capacity(req.getCapacity())
                .status(TableStatus.FREE)
                .build();
        tableRepo.save(t);

        TableResponse res = new TableResponse(t.getId(), t.getCode(), t.getCapacity(), t.getStatus());
        tableEvents.tableChanged(t.getId(), t.getCode(), t.getCapacity(), t.getStatus().name(), "CREATED");
        return res;
    }

    // Cập nhật thông tin bàn
    @Transactional
    public TableResponse update(Long id, UpdateTableRequest req) {
        RestaurantTable t = tableRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bàn."));

        if (!t.getCode().equals(req.getCode()) && tableRepo.existsByCode(req.getCode())) {
            throw new ConflictException("Mã bàn đã tồn tại.");
        }

        t.setCode(req.getCode());
        t.setCapacity(req.getCapacity());

        TableResponse res = new TableResponse(t.getId(), t.getCode(), t.getCapacity(), t.getStatus());
        tableEvents.tableChanged(t.getId(), t.getCode(), t.getCapacity(), t.getStatus().name(), "UPDATED");
        return res;
    }

    @Transactional
    public void delete(Long id) {
        RestaurantTable t = tableRepo.findById(id)
            .orElseThrow(() -> new NotFoundException("Không tìm thấy bàn."));

        if (orderRepo.existsByTable_Id(id)) {
            throw new BadRequestException("Không thể xóa. Bàn đã từng có order, không được phép xóa.");
        }

        tableRepo.delete(t);
        tableEvents.tableDeleted(id, t.getCode());
    }



    @Transactional(readOnly = true)
    public Page<TableResponse> list(int page, int size, String keyword, String status) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("code").ascending());

        Page<RestaurantTable> p;

        // Nếu có trạng thái
        if (status != null && !status.isBlank()) {
            TableStatus tableStatus = TableStatus.valueOf(status);

            // Nếu keyword là số => lọc capacity >= số đó
            if (keyword != null && keyword.matches("\\d+")) {
                int capacity = Integer.parseInt(keyword);
                p = tableRepo.findByStatusAndCapacityGreaterThanEqual(tableStatus, capacity, pageable);
            }
            // Nếu keyword là chữ => lọc code
            else if (keyword != null && !keyword.isBlank()) {
                p = tableRepo.findByStatusAndCodeContainingIgnoreCase(tableStatus, keyword, pageable);
            } else {
                p = tableRepo.findByStatus(tableStatus, pageable);
            }
        }
        // Không có filter trạng thái
        else {
            if (keyword != null && keyword.matches("\\d+")) {
                int capacity = Integer.parseInt(keyword);
                p = tableRepo.findByCapacityGreaterThanEqual(capacity, pageable);
            } else if (keyword != null && !keyword.isBlank()) {
                p = tableRepo.findByCodeContainingIgnoreCase(keyword, pageable);
            } else {
                p = tableRepo.findAll(pageable);
            }
        }

        return p.map(t -> new TableResponse(t.getId(), t.getCode(), t.getCapacity(), t.getStatus()));
    }


}
