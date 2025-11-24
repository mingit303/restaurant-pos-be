package com.example.restaurant.dto.order.response;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
@Data
public class OrderResponse {
    private Long id;
    private TableInfo table;
    private String status;
    private WaiterInfo waiter;
    private List<Item> items;
    private BigDecimal subtotal, discount, total;
    private String formattedTotal;

    @Data
    public static class TableInfo {
        private Long id; 
        private String code; 
        private String status;
        
        public TableInfo() {}
        
        public TableInfo(Long id,String code,String status){
            this.id=id;
            this.code=code;
            this.status=status;
        }
    }

    @Data
    public static class WaiterInfo {
        private Long id; 
        private String fullName;
        
        public WaiterInfo() {}
        
        public WaiterInfo(Long id,String fullName){
            this.id = id;
            this.fullName = fullName;
        }
    }

    @Data
    public static class Item {
        private Long id, menuItemId; 
        private String name; 
        private BigDecimal unitPrice; 
        private Integer quantity; 
        private BigDecimal lineTotal; 
        private String note; 
        private String state; 
        private String formattedPrice;
        public Item() {}
        public Item(Long id,Long menuItemId,String name,BigDecimal unitPrice,Integer quantity,BigDecimal lineTotal,String note,String state,String formattedPrice){
            this.id = id;
            this.menuItemId = menuItemId;
            this.name = name;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.lineTotal = lineTotal;
            this.note = note;
            this.state = state;
            this.formattedPrice = formattedPrice;
        }
    }

    public OrderResponse() {}
    public OrderResponse(Long id, TableInfo table, String status, WaiterInfo waiter, List<Item> items,
                         BigDecimal subtotal, BigDecimal discount, BigDecimal total, String formattedTotal) {
        this.id = id; 
        this.table = table; 
        this.status = status; 
        this.waiter = waiter; 
        this.items = items;
        this.subtotal = subtotal; 
        this.discount = discount; 
        this.total = total; 
        this.formattedTotal = formattedTotal;
    }
}
