package com.acme.parking.api.model;

import com.acme.parking.inventory.model.ParkingSpace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Simple wrapper used to filtering query results
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParkingSpaceQueryResult {

  private List<ParkingSpace> result;
}
