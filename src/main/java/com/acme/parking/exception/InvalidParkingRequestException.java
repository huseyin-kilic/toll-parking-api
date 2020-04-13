package com.acme.parking.exception;

public class InvalidParkingRequestException extends RuntimeException {

  public InvalidParkingRequestException(String message) {
    super(message);
  }
}
