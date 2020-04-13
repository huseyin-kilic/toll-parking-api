package com.acme.parking.billing.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

/**
 * Configuration values for pricing & billing operations.
 *
 * @see com.acme.parking.billing.BillingController
 */
@Component
@ConfigurationProperties("app.pricing")
@Validated
@Data
public class PricingStrategy {

  @Pattern(regexp = "WITH_FIXED_AMOUNT|DURATION_ONLY")
  private String strategy;

  @Min(0)
  private double fixedAmount;

  @Min(0)
  private double pricePerSecond;
}
