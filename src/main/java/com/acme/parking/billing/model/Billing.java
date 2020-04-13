package com.acme.parking.billing.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import static io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY;

/**
 * Represents a billing instance, for now only with the price information.
 *
 * @see com.acme.parking.inventory.model.Parking
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Billing {

  @Schema(description = "Unique identifier", example = "90", minimum = "0", required = true, accessMode = READ_ONLY)
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Schema(description = "Currency of the price", example = "EUR", required = true, accessMode = READ_ONLY)
  private String currency;

  @Schema(description = "Actual price for the parking", example = "5.6", required = true, accessMode = READ_ONLY)
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Double amount;

  public Billing(String currency, Double amount) {
    this.currency = currency;
    this.amount = amount;
  }
}
