package com.acme.parking.api.model;

import com.acme.parking.inventory.model.ParkingSpace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Simple wrapper used to perform parking space filtering queries
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSpaceQuery {

  private ParkingSpace.Type type;
  private ParkingSpace.Status status;
  private Integer count;
}
