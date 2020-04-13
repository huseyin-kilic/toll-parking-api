package com.acme.parking.exception;

public class InvalidParkingSpaceIdException extends RuntimeException {

  public InvalidParkingSpaceIdException(Long id) {
    super(String.format("Invalid parking space id %d", id));
  }
}
