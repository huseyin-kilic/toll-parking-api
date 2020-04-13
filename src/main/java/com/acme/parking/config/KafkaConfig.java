package com.acme.parking.config;

import com.acme.parking.api.model.ParkingSpaceQuery;
import com.acme.parking.api.model.ParkingSpaceQueryResult;
import com.acme.parking.billing.model.Billing;
import com.acme.parking.inventory.model.Parking;
import com.acme.parking.inventory.model.ParkingSpace;
import com.acme.parking.properties.KafkaReplyTopics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

/**
 * Configuration bean to configure Kafka templates
 */
@Configuration
public class KafkaConfig {

  @Value("${kafka.group.id}")
  private String groupId;

  private final KafkaReplyTopics replyTopics;

  @Autowired
  public KafkaConfig(KafkaReplyTopics replyTopics) {
    this.replyTopics = replyTopics;
  }

  // Kafka Request-Reply Templates for Get Parking Space By Id
  @Bean
  public ReplyingKafkaTemplate<String, Long, ParkingSpace> getParkingSpaceByIdReplyingKafkaTemplate(ProducerFactory<String, Long> pf,
                                                                                                    ConcurrentKafkaListenerContainerFactory<String, ParkingSpace> factory) {
    ConcurrentMessageListenerContainer<String, ParkingSpace> replyContainer
      = factory.createContainer(replyTopics.getParkingSpaceById());
    setReplyContainerProperties(replyContainer.getContainerProperties());
    return new ReplyingKafkaTemplate<>(pf, replyContainer);
  }

  @Bean
  public KafkaTemplate<String, ParkingSpace> getSingleParkingSpaceReplyTemplate(ProducerFactory<String, ParkingSpace> pf,
                                                                                ConcurrentKafkaListenerContainerFactory<String, ParkingSpace> factory) {

    KafkaTemplate<String, ParkingSpace> kafkaTemplate = new KafkaTemplate<>(pf);
    setFactoryContainerProperties(factory.getContainerProperties());
    factory.setReplyTemplate(kafkaTemplate);
    return kafkaTemplate;
  }

  // Kafka Request-Reply Templates for Next Available Parking Space
  // (No need to define the reply template, as it is already defined in getSingleParkingSpaceReplyTemplate method)
  @Bean
  public ReplyingKafkaTemplate<String, ParkingSpace.Type, ParkingSpace> nextAvailableReplyingKafkaTemplate(ProducerFactory<String, ParkingSpace.Type> pf,
                                                                                                           ConcurrentKafkaListenerContainerFactory<String, ParkingSpace> factory) {
    ConcurrentMessageListenerContainer<String, ParkingSpace> replyContainer
      = factory.createContainer(replyTopics.getNextAvailableParkingSpace());
    setReplyContainerProperties(replyContainer.getContainerProperties());
    return new ReplyingKafkaTemplate<>(pf, replyContainer);
  }

  //Kafka Request-Reply Templates for Parking Spaces Query
  @Bean
  public ReplyingKafkaTemplate<String, ParkingSpaceQuery, ParkingSpaceQueryResult> parkingSpacesQueryReplyingKafkaTemplate(ProducerFactory<String, ParkingSpaceQuery> pf,
                                                                                                                           ConcurrentKafkaListenerContainerFactory<String, ParkingSpaceQueryResult> factory) {
    ConcurrentMessageListenerContainer<String, ParkingSpaceQueryResult> replyContainer
      = factory.createContainer(replyTopics.getParkingSpacesQuery());
    setReplyContainerProperties(replyContainer.getContainerProperties());
    return new ReplyingKafkaTemplate<>(pf, replyContainer);
  }

  @Bean
  public KafkaTemplate<String, ParkingSpaceQueryResult> parkingSpacesQueryReplyTemplate(ProducerFactory<String, ParkingSpaceQueryResult> pf,
                                                                                        ConcurrentKafkaListenerContainerFactory<String, ParkingSpaceQueryResult> factory) {
    KafkaTemplate<String, ParkingSpaceQueryResult> kafkaTemplate = new KafkaTemplate<>(pf);
    setFactoryContainerProperties(factory.getContainerProperties());
    factory.setReplyTemplate(kafkaTemplate);
    return kafkaTemplate;
  }

  //Kafka Request-Reply Templates for Billing Calculation
  @Bean
  public ReplyingKafkaTemplate<String, Parking, Billing> calculateBillingReplyingKafkaTemplate(ProducerFactory<String, Parking> pf,
                                                                                               ConcurrentKafkaListenerContainerFactory<String, Billing> factory) {
    ConcurrentMessageListenerContainer<String, Billing> replyContainer
      = factory.createContainer(replyTopics.getBillingCalculation());
    setReplyContainerProperties(replyContainer.getContainerProperties());
    return new ReplyingKafkaTemplate<>(pf, replyContainer);
  }

  @Bean
  public KafkaTemplate<String, Billing> calculateBillingReplyTemplate(ProducerFactory<String, Billing> pf,
                                                                      ConcurrentKafkaListenerContainerFactory<String, Billing> factory) {
    KafkaTemplate<String, Billing> kafkaTemplate = new KafkaTemplate<>(pf);
    setFactoryContainerProperties(factory.getContainerProperties());
    factory.setReplyTemplate(kafkaTemplate);
    return kafkaTemplate;
  }

  // Kafka Request-Reply Templates for Get Parking By Id
  @Bean(name = "parkingById")
  public ReplyingKafkaTemplate<String, Long, Parking> getParkingByIdReplyingKafkaTemplate(ProducerFactory<String, Long> pf,
                                                                                          ConcurrentKafkaListenerContainerFactory<String, Parking> factory) {
    ConcurrentMessageListenerContainer<String, Parking> replyContainer
      = factory.createContainer(replyTopics.getParkingById());
    setReplyContainerProperties(replyContainer.getContainerProperties());
    return new ReplyingKafkaTemplate<>(pf, replyContainer);
  }

  @Bean
  public KafkaTemplate<String, Parking> getParkingReplyTemplate(ProducerFactory<String, Parking> pf,
                                                                ConcurrentKafkaListenerContainerFactory<String, Parking> factory) {

    KafkaTemplate<String, Parking> kafkaTemplate = new KafkaTemplate<>(pf);
    setFactoryContainerProperties(factory.getContainerProperties());
    factory.setReplyTemplate(kafkaTemplate);
    return kafkaTemplate;
  }

  // Kafka Request-Reply Templates for Parking Start
  // (No need to define the reply template, as it is already defined in getParkingReplyTemplate method)
  @Bean
  public ReplyingKafkaTemplate<String, Parking, Parking> getParkingStartReplyingKafkaTemplate(ProducerFactory<String, Parking> pf,
                                                                                              ConcurrentKafkaListenerContainerFactory<String, Parking> factory) {
    ConcurrentMessageListenerContainer<String, Parking> replyContainer
      = factory.createContainer(replyTopics.getParkingStart());
    setReplyContainerProperties(replyContainer.getContainerProperties());
    return new ReplyingKafkaTemplate<>(pf, replyContainer);
  }

  // Kafka Request-Reply Templates for Get Parking By Id
  @Bean(name = "parkingCompletion")
  public ReplyingKafkaTemplate<String, Long, Parking> getParkingCompletionReplyingKafkaTemplate(ProducerFactory<String, Long> pf,
                                                                                                ConcurrentKafkaListenerContainerFactory<String, Parking> factory) {
    ConcurrentMessageListenerContainer<String, Parking> replyContainer
      = factory.createContainer(replyTopics.getParkingCompletion());
    setReplyContainerProperties(replyContainer.getContainerProperties());
    return new ReplyingKafkaTemplate<>(pf, replyContainer);
  }
  // (No need to define the reply template, as it is already defined in getParkingReplyTemplate method)


  //Common Configuration Methods
  private void setReplyContainerProperties(ContainerProperties containerProperties) {
    containerProperties.setMissingTopicsFatal(false);
    containerProperties.setGroupId(groupId);
    containerProperties.setAckOnError(true);
  }

  private void setFactoryContainerProperties(ContainerProperties containerProperties) {
    containerProperties.setMissingTopicsFatal(false);
    containerProperties.setAckOnError(true);
  }

}