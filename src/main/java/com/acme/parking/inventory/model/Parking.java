package com.acme.parking.inventory.model;

import com.acme.parking.billing.model.Billing;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * Represents a parking action at a given parking space
 * Start time is set automatically by the system at the creation time
 * End time and billing information are set automatically by the system at the completion time
 * <p>
 * For a parking space entity, please refer to {@link ParkingSpace}
 * <p>
 * For all operations available, see {@link com.acme.parking.api.ParkingAPI}
 */
@Data
@Entity
@AllArgsConstructor
public class Parking {

  @Schema(description = "Unique identifier", example = "8", minimum = "0", required = true, accessMode = READ_ONLY)
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Schema(description = "Parking space where the parking takes place", example = "8", minimum = "1", required = true)
  private Long parkingSpaceId;

  @Schema(description = "Start time of the parking", example = "2020-04-13T07:30:00.000Z", required = true, accessMode = READ_ONLY)
  @JsonProperty(access = Access.READ_ONLY)
  private final LocalDateTime startDateTime;

  @Schema(description = "End time of the parking", example = "2020-04-13T08:30:00.000Z", accessMode = READ_ONLY)
  @JsonInclude(Include.NON_NULL)
  private LocalDateTime endDateTime;

  @Schema(description = "Duration of the parking in seconds", example = "3600", accessMode = READ_ONLY)
  @JsonInclude(Include.NON_NULL)
  private Long durationSeconds;

  @Schema(description = "Billing information calculated when the parking is completed", accessMode = READ_ONLY)
  @JsonInclude(Include.NON_NULL)
  @OneToOne(cascade = CascadeType.ALL)
  private Billing billing;

  public Parking() {
    this.startDateTime = LocalDateTime.now();
  }
}
