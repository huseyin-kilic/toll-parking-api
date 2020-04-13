package com.acme.parking.exception;

public class InvalidParkingSpaceStatusException extends RuntimeException {

  public InvalidParkingSpaceStatusException(Long id, String status) {
    super(String.format("Parking space #%d is already %s" , id, status));
  }
}
