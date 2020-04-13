package com.acme.parking.inventory.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * Represents a single parking space entity with a given type
 * The current availability status is reflected in the status filed
 *
 * For the actual parking entity, see {@link Parking}
 *
 * For all operations available, see {@link com.acme.parking.api.ParkingSpaceAPI}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ParkingSpace {

  @Schema(description = "Unique identifier", example = "15", minimum = "1", required = true)
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Schema(description = "Type of the cars allowed to park in this parking space", accessMode = READ_ONLY)
  private Type type;

  @Schema(description = "Current availability status", defaultValue = "AVAILABLE", accessMode = READ_ONLY)
  private Status status;

  @Schema(description = "Details regarding the ongoing parking in this parking space", accessMode = READ_ONLY)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @OneToOne(cascade = CascadeType.ALL)
  private Parking currentParkingInformation;

  public ParkingSpace(Long id, Type type) {
    this.id = id;
    this.type = type;
    this.status = Status.AVAILABLE;
  }

  public enum Status {
    AVAILABLE,
    OCCUPIED
  }

  public enum Type {
    GASOLINE,
    KW20,
    KW50
  }
}
