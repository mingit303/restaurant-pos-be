package com.example.restaurant.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); } // Error: 409
}
