package com.example.restaurant.mapper;

import com.example.restaurant.domain.voucher.Voucher;
import com.example.restaurant.dto.voucher.request.VoucherRequest;
import com.example.restaurant.dto.voucher.response.VoucherResponse;

public class VoucherMapper {
    public static VoucherResponse toResponse(Voucher v){
        if(v==null) return null;
        return new VoucherResponse(
                v.getId(), v.getCode(), v.getDescription(), v.getDiscountPercent(), v.getMaxDiscount(),
                v.getStartDate(), v.getEndDate(), v.getUsageLimit(), v.getUsedCount(), v.isActive()
        );
    }

    public static void apply(Voucher v, VoucherRequest req){
        v.setCode(req.getCode().trim());
        v.setDescription(req.getDescription());
        v.setDiscountPercent(req.getDiscountPercent());
        v.setMaxDiscount(req.getMaxDiscount());
        v.setStartDate(req.getStartDate());
        v.setEndDate(req.getEndDate());
        v.setUsageLimit(req.getUsageLimit());
        if (req.getActive()!=null) v.setActive(req.getActive());
    }
}
