    package com.example.restaurant.service.voucher;

    import com.example.restaurant.domain.voucher.Voucher;
    import com.example.restaurant.dto.voucher.request.VoucherRequest;
    import com.example.restaurant.dto.voucher.response.VoucherCheckResponse;
    import com.example.restaurant.dto.voucher.response.VoucherResponse;
    import com.example.restaurant.exception.BadRequestException;
    import com.example.restaurant.exception.ConflictException;
    import com.example.restaurant.exception.NotFoundException;
    import com.example.restaurant.mapper.VoucherMapper;
import com.example.restaurant.repository.invoice.InvoiceRepository;
import com.example.restaurant.repository.voucher.VoucherRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.*;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.math.BigDecimal;
    import java.time.LocalDate;
import java.util.List;

    @Service @RequiredArgsConstructor
public class VoucherService {
    private final VoucherRepository repo;
    private final InvoiceRepository invoiceRepo;

    @Transactional(readOnly = true)
    public Page<VoucherResponse> search(int page, int size, String keyword, Boolean active, LocalDate from, LocalDate to) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        Page<Voucher> pageEntity = repo.findAll(pageable);

        var list = pageEntity.stream()
                .map(VoucherMapper::toResponse)
                .filter(v -> {
                    boolean ok = true;
                    if (keyword != null && !keyword.isBlank()) {
                        String k = keyword.toLowerCase();
                        ok &= (v.getCode() != null && v.getCode().toLowerCase().contains(k))
                        || (v.getDescription() != null && v.getDescription().toLowerCase().contains(k));
                    }
                    if (active != null) ok &= (v.isActive() == active);
                    if (from != null) ok &= (v.getStartDate() == null || !v.getStartDate().isAfter(to != null ? to : LocalDate.MAX));
                    if (to != null) ok &= (v.getEndDate() == null || !v.getEndDate().isBefore(from != null ? from : LocalDate.MIN));
                    return ok;
                })
                .toList();

        return new PageImpl<>(list, pageable, pageEntity.getTotalElements());
    }

    @Transactional
    public VoucherResponse create(VoucherRequest req){
        validateDates(req);
        if (repo.existsByCodeIgnoreCase(req.getCode()))
            throw new ConflictException("Mã voucher đã tồn tại.");
        Voucher v = Voucher.builder().build();
        VoucherMapper.apply(v, req);
        v.setUsedCount(0);
        if (!v.isActive() && req.getActive()==null) v.setActive(true);
        return VoucherMapper.toResponse(repo.save(v));
    }


        @Transactional(readOnly = true)
        public VoucherResponse getById(Long id){
            return repo.findById(id).map(VoucherMapper::toResponse)
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy voucher."));
        }

        @Transactional
        public VoucherResponse update(Long id, VoucherRequest req){
            validateDates(req);
            Voucher v = repo.findById(id).orElseThrow(() -> new NotFoundException("Không tìm thấy voucher."));
            if (!v.getCode().equalsIgnoreCase(req.getCode()) && repo.existsByCodeIgnoreCase(req.getCode()))
                throw new ConflictException("Mã voucher đã tồn tại.");
            VoucherMapper.apply(v, req);
            return VoucherMapper.toResponse(repo.save(v));
        }

        @Transactional
        public void delete(Long id) {
            Voucher v = repo.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy voucher."));

            if (invoiceRepo.existsByVoucher_Id(id)) {
                throw new BadRequestException("Không thể xóa voucher đã được sử dụng.");
            }

            repo.delete(v);
        }

        /** API check voucher cho Cashier/Waiter trước khi tạo hoá đơn */
        @Transactional(readOnly = true)
        public VoucherCheckResponse check(String code){
            Voucher v = repo.findByCodeIgnoreCase(code)
                    .orElseThrow(() -> new NotFoundException("Mã voucher không tồn tại."));
            String msg = validateUsable(v);
            boolean valid = (msg==null);
            return new VoucherCheckResponse(valid, valid? "OK" : msg, v.getCode(), v.getDiscountPercent(), v.getMaxDiscount());
        }

        /** Gọi khi invoice thanh toán thành công để tăng usedCount */
        @Transactional
        public void increaseUsage(String code){
            if (code==null || code.isBlank()) return;
            Voucher v = repo.findByCodeIgnoreCase(code).orElseThrow();
            v.setUsedCount(v.getUsedCount()==null?1:v.getUsedCount()+1);
            repo.save(v);
        }

        // ===== Helpers =====
        private void validateDates(VoucherRequest req){
            if (req.getStartDate()!=null && req.getEndDate()!=null
                    && req.getEndDate().isBefore(req.getStartDate())) {
                throw new BadRequestException("endDate phải >= startDate.");
            }
        }

        /** null = usable, otherwise return reason */
        private String validateUsable(Voucher v){
            LocalDate today = LocalDate.now();
            if (!v.isActive()) return "Voucher đã bị tắt.";
            if (v.getStartDate()!=null && today.isBefore(v.getStartDate())) return "Voucher chưa bắt đầu.";
            if (v.getEndDate()!=null && today.isAfter(v.getEndDate())) return "Voucher đã hết hạn.";
            if (v.getUsageLimit()!=null && v.getUsedCount()!=null && v.getUsedCount() >= v.getUsageLimit()) return "Voucher đã hết lượt sử dụng.";
            if (v.getDiscountPercent()==null || v.getDiscountPercent().compareTo(BigDecimal.ZERO) <= 0) return "Voucher không hợp lệ.";
            return null;
        }

        @Transactional(readOnly = true)
        public List<VoucherCheckResponse> getUsableVouchers() {
            return repo.findAll().stream()
                    .filter(v -> validateUsable(v) == null) // chỉ voucher hợp lệ
                    .map(v -> new VoucherCheckResponse(
                            true,
                            "OK",
                            v.getCode(),
                            v.getDiscountPercent(),
                            v.getMaxDiscount()
                    ))
                    .toList();
        }

    }
