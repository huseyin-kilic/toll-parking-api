package com.acme.parking.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Placeholders for Kafka request topics
 */
@Component
@ConfigurationProperties(KafkaRequestTopics.KAFKA_REQUEST_TOPIC_PREFIX)
@Data
public class KafkaRequestTopics {

  public static final String KAFKA_REQUEST_TOPIC_PREFIX = "kafka.topic.request";

  private String parkingSpaceById;
  private String parkingSpacesQuery;
  private String nextAvailableParkingSpace;

  private String parkingById;
  private String parkingStart;
  private String parkingCompletion;
  private String billingCalculation;

}
