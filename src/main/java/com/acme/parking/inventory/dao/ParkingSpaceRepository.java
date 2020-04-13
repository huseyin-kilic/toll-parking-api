package com.acme.parking.inventory.dao;


import com.acme.parking.inventory.model.ParkingSpace;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Data access object exposing persistence operations on {@link ParkingSpace}
 */
public interface ParkingSpaceRepository extends PagingAndSortingRepository<ParkingSpace, Integer> {

  ParkingSpace findById(Long id);

  List<ParkingSpace> findByTypeAndStatus(ParkingSpace.Type type, ParkingSpace.Status status);
}
