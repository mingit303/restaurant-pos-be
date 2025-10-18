// dto/order/OrderResponse.java
package com.example.restaurant.dto.order.response;

import java.math.BigDecimal;
import java.util.List;

public class OrderResponse {
    private Long id;
    private TableInfo table;
    private String status;
    private WaiterInfo waiter;
    private List<Item> items;
    private BigDecimal subtotal, discount, total;
    private String formattedTotal;

    public static class TableInfo {
        private Long id; private String code; private String status;
        public TableInfo() {}
        public TableInfo(Long id,String code,String status){this.id=id;this.code=code;this.status=status;}
        public Long getId(){return id;} public void setId(Long v){this.id=v;}
        public String getCode(){return code;} public void setCode(String v){this.code=v;}
        public String getStatus(){return status;} public void setStatus(String v){this.status=v;}
    }
    public static class WaiterInfo {
        private Long id; private String fullName;
        public WaiterInfo() {}
        public WaiterInfo(Long id,String fullName){this.id=id;this.fullName=fullName;}
        public Long getId(){return id;} public void setId(Long v){this.id=v;}
        public String getFullName(){return fullName;} public void setFullName(String v){this.fullName=v;}
    }
    public static class Item {
        private Long id, menuItemId; private String name; 
        private BigDecimal unitPrice; private Integer quantity; 
        private BigDecimal lineTotal; private String note; private String state; private String formattedPrice;
        public Item() {}
        public Item(Long id,Long menuItemId,String name,BigDecimal unitPrice,Integer quantity,BigDecimal lineTotal,String note,String state,String formattedPrice){
            this.id=id;this.menuItemId=menuItemId;this.name=name;this.unitPrice=unitPrice;this.quantity=quantity;this.lineTotal=lineTotal;this.note=note;this.state=state;this.formattedPrice=formattedPrice;
        }
        public Long getId(){return id;} public void setId(Long v){this.id=v;}
        public Long getMenuItemId(){return menuItemId;} public void setMenuItemId(Long v){this.menuItemId=v;}
        public String getName(){return name;} public void setName(String v){this.name=v;}
        public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){this.unitPrice=v;}
        public Integer getQuantity(){return quantity;} public void setQuantity(Integer v){this.quantity=v;}
        public BigDecimal getLineTotal(){return lineTotal;} public void setLineTotal(BigDecimal v){this.lineTotal=v;}
        public String getNote(){return note;} public void setNote(String v){this.note=v;}
        public String getState(){return state;} public void setState(String v){this.state=v;}
        public String getFormattedPrice(){return formattedPrice;} public void setFormattedPrice(String v){this.formattedPrice=v;}
    }

    public OrderResponse() {}
    public OrderResponse(Long id, TableInfo table, String status, WaiterInfo waiter, List<Item> items,
                         BigDecimal subtotal, BigDecimal discount, BigDecimal total, String formattedTotal) {
        this.id=id; this.table=table; this.status=status; this.waiter=waiter; this.items=items;
        this.subtotal=subtotal; this.discount=discount; this.total=total; this.formattedTotal=formattedTotal;
    }
    // getters/setters
    public Long getId(){return id;} public void setId(Long v){this.id=v;}
    public TableInfo getTable(){return table;} public void setTable(TableInfo v){this.table=v;}
    public String getStatus(){return status;} public void setStatus(String v){this.status=v;}
    public WaiterInfo getWaiter(){return waiter;} public void setWaiter(WaiterInfo v){this.waiter=v;}
    public List<Item> getItems(){return items;} public void setItems(List<Item> v){this.items=v;}
    public BigDecimal getSubtotal(){return subtotal;} public void setSubtotal(BigDecimal v){this.subtotal=v;}
    public BigDecimal getDiscount(){return discount;} public void setDiscount(BigDecimal v){this.discount=v;}
    public BigDecimal getTotal(){return total;} public void setTotal(BigDecimal v){this.total=v;}
    public String getFormattedTotal(){return formattedTotal;} public void setFormattedTotal(String v){this.formattedTotal=v;}
}
