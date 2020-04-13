package com.acme.parking.properties;

import com.acme.parking.billing.model.PricingStrategy;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

/**
 * Property manager class for user-defined values
 * For pricing parameters please refer to {@link PricingStrategy}
 */
@Component
@ConfigurationProperties("app")
@Validated
@Data
public class AppProperties {

  @Min(0)
  private int typeGasolineCount;

  @Min(0)
  private int typeKW20Count;

  @Min(0)
  private int typeKW50Count;
}
