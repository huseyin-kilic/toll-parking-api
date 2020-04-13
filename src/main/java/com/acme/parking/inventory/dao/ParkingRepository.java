package com.acme.parking.inventory.dao;


import com.acme.parking.inventory.model.Parking;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Data access object exposing persistence operations on {@link Parking}
 */
public interface ParkingRepository extends PagingAndSortingRepository<Parking, Integer> {

  Parking findById(Long id);

}
