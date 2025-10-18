package com.example.restaurant.dto.order.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateItemStateRequest {
    @NotBlank private String state;
    public String getState(){ return state; }
    public void setState(String state){ this.state=state; }
}
