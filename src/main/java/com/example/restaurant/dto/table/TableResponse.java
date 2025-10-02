package com.example.restaurant.dto.table;                  // 1

import com.example.restaurant.domain.table.TableStatus;    // 2

public record TableResponse(                               // 3
        Long id,                                           // 4
        String code,                                       // 5
        Integer capacity,                                  // 6
        TableStatus status                                 // 7
) {}