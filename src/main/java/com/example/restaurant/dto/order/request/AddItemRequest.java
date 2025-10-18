package com.example.restaurant.dto.order.request;

import jakarta.validation.constraints.*;

public class AddItemRequest {
    @NotNull private Long menuItemId;
    @NotNull @Positive private Integer quantity;
    private String note;

    public Long getMenuItemId(){ return menuItemId; }
    public void setMenuItemId(Long id){ this.menuItemId=id; }
    public Integer getQuantity(){ return quantity; }
    public void setQuantity(Integer q){ this.quantity=q; }
    public String getNote(){ return note; }
    public void setNote(String note){ this.note=note; }
}
