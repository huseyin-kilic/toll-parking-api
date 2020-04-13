package com.acme.parking.exception;

public class InvalidParkingIdException extends RuntimeException {

  public InvalidParkingIdException(Long id) {
    super(String.format("Invalid parking id %d", id));
  }
}
