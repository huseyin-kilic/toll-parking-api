package com.acme.parking.billing;

import com.acme.parking.billing.model.Billing;
import com.acme.parking.billing.model.PricingStrategy;
import com.acme.parking.inventory.InventoryController;
import com.acme.parking.inventory.model.Parking;
import com.acme.parking.inventory.model.ParkingSpace;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import static com.acme.parking.properties.KafkaRequestTopics.KAFKA_REQUEST_TOPIC_PREFIX;

/**
 * Responsible for billing-related operations.
 * Incoming & outgoing communication is managed by Kafka topics.
 * Called by {@link InventoryController} to calculate the billing information.
 * <p>
 * Pricing calculation is performed using business Rules, available in the classpath resources (src/main/resources/PricingRules.drl)
 * Powered by Drools rule engine.
 */
@Component
public class BillingController {

  private final KieContainer kieContainer;
  private final PricingStrategy pricingStrategy;

  @Autowired
  public BillingController(KieContainer kieContainer, PricingStrategy pricingStrategy) {
    this.kieContainer = kieContainer;
    this.pricingStrategy = pricingStrategy;
  }

  @KafkaListener(topics = "${" + KAFKA_REQUEST_TOPIC_PREFIX + ".billingCalculation}", groupId = "${kafka.group.id}")
  @SendTo
  public Billing calculateBilling(Parking parking) {
    KieSession kieSession = kieContainer.newKieSession();
    kieSession.insert(parking);
    kieSession.insert(pricingStrategy);
    kieSession.fireAllRules();

    ObjectFilter objectFilter = new ObjectFilter() {
      @Override
      public boolean accept(Object o) {
        return "Billing".equals(o.getClass().getSimpleName());
      }
    };
    return (Billing) kieSession.getObjects(objectFilter).stream()
      .findFirst().get();
  }

}
