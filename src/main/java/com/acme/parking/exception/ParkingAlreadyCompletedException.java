package com.acme.parking.exception;

public class ParkingAlreadyCompletedException extends RuntimeException {

  public ParkingAlreadyCompletedException(Long id) {
    super(String.format("Parking #%d is already completed", id));
  }
}
