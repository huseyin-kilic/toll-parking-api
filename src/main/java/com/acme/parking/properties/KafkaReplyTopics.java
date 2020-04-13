package com.acme.parking.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Placeholders for Kafka reply topics
 */
@Component
@ConfigurationProperties(KafkaReplyTopics.KAFKA_REPLY_TOPIC_PREFIX)
@Data
public class KafkaReplyTopics {

  public static final String KAFKA_REPLY_TOPIC_PREFIX = "kafka.topic.reply";

  private String parkingSpaceById;
  private String parkingSpacesQuery;
  private String nextAvailableParkingSpace;

  private String parkingById;
  private String parkingStart;
  private String parkingCompletion;
  private String billingCalculation;

}
