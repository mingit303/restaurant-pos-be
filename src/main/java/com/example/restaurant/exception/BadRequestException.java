package com.example.restaurant.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message)  { 
        super(message); 
    } // 1: 400
}
