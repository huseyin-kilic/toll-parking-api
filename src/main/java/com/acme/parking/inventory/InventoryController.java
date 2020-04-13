package com.acme.parking.inventory;

import com.acme.parking.api.ParkingSpaceAPI;
import com.acme.parking.api.model.ParkingSpaceQuery;
import com.acme.parking.api.model.ParkingSpaceQueryResult;
import com.acme.parking.billing.BillingController;
import com.acme.parking.billing.model.Billing;
import com.acme.parking.exception.InvalidParkingIdException;
import com.acme.parking.exception.InvalidParkingSpaceIdException;
import com.acme.parking.inventory.dao.ParkingRepository;
import com.acme.parking.inventory.dao.ParkingSpaceRepository;
import com.acme.parking.inventory.model.Parking;
import com.acme.parking.inventory.model.ParkingSpace;
import com.acme.parking.inventory.model.ParkingSpace.Status;
import com.acme.parking.properties.KafkaRequestTopics;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.acme.parking.properties.KafkaRequestTopics.KAFKA_REQUEST_TOPIC_PREFIX;
import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Responsible for management of Parking Spaces.
 * Exposes operations such as query by id, query by type and status, parking start and completion.
 * Incoming & outgoing communication is managed by Kafka topics.
 * <p>
 * {@link ParkingSpaceAPI} and {@link com.acme.parking.api.ParkingAPI} send requests to {@link InventoryController} to perform inventory-related operations.
 * {@link InventoryController} sends requests to {@link BillingController} to perform billing-related operations.
 */
@Component
public class InventoryController {

  private final KafkaRequestTopics requestTopics;

  private final Map<ParkingSpace.Type, Long> nextAvailableParkingSpaceCache = new HashMap<>();

  private final ParkingSpaceRepository parkingSpaceRepository;

  private final ParkingRepository parkingRepository;

  private final ReplyingKafkaTemplate<String, Parking, Billing> billingCalculationKafkaTemplate;

  @Autowired
  public InventoryController(KafkaRequestTopics requestTopics, ParkingSpaceRepository parkingSpaceRepository,
                             ParkingRepository parkingRepository,
                             ReplyingKafkaTemplate<String, Parking, Billing> billingCalculationKafkaTemplate) {
    this.requestTopics = requestTopics;
    this.parkingSpaceRepository = parkingSpaceRepository;
    this.parkingRepository = parkingRepository;
    this.billingCalculationKafkaTemplate = billingCalculationKafkaTemplate;
  }

  @KafkaListener(topics = "${" + KAFKA_REQUEST_TOPIC_PREFIX + ".parkingSpaceById}", groupId = "${kafka.group.id}")
  @SendTo
  public ParkingSpace getParkingSpaceById(Long id) throws InvalidParkingSpaceIdException {
    ParkingSpace parkingSpace = parkingSpaceRepository.findById(id);
    if (parkingSpace == null) {
      return new ParkingSpace();
    }
    return parkingSpace;
  }

  @KafkaListener(topics = "${" + KAFKA_REQUEST_TOPIC_PREFIX + ".nextAvailableParkingSpace}", groupId = "${kafka.group.id}")
  @SendTo
  public ParkingSpace getNextAvailableParkingSpace(ParkingSpace.Type type) {
    if (nextAvailableParkingSpaceCache.get(type) == null) {
      updateNextAvailableCache(type);
    }
    return new ParkingSpace(nextAvailableParkingSpaceCache.get(type), type);
  }

  @KafkaListener(topics = "${" + KAFKA_REQUEST_TOPIC_PREFIX + ".parkingSpacesQuery}", groupId = "${kafka.group.id}")
  @SendTo
  public ParkingSpaceQueryResult parkingSpacesQuery(ParkingSpaceQuery query) {
    //TODO paging
    List<ParkingSpace> result = parkingSpaceRepository.findByTypeAndStatus(query.getType(), query.getStatus());
    if (query.getCount() < result.size()) {
      result = result.subList(0, query.getCount());
    }
    return new ParkingSpaceQueryResult(result);
  }


  @KafkaListener(topics = "${" + KAFKA_REQUEST_TOPIC_PREFIX + ".parkingStart}", groupId = "${kafka.group.id}")
  @SendTo
  public Parking startParking(Parking parking) {
    Parking parkingCreated = parkingRepository.save(parking);
    ParkingSpace parkingSpace = parkingSpaceRepository.findById(parking.getParkingSpaceId());

    parkingSpace.setStatus(Status.OCCUPIED);
    parkingSpace.setCurrentParkingInformation(parkingCreated);
    parkingSpaceRepository.save(parkingSpace);
    updateNextAvailableCache(parkingSpace.getType());

    return parkingCreated;
  }


  @KafkaListener(topics = "${" + KAFKA_REQUEST_TOPIC_PREFIX + ".parkingById}", groupId = "${kafka.group.id}")
  @SendTo
  public Parking getParkingById(Long id) throws InvalidParkingIdException {
    Parking parking = parkingRepository.findById(id);
    if (parking == null) {
      return new Parking();
    }
    return parking;
  }

  @KafkaListener(topics = "${" + KAFKA_REQUEST_TOPIC_PREFIX + ".parkingCompletion}", groupId = "${kafka.group.id}")
  @SendTo
  public Parking endParking(Long id) {
    Parking completedParking = parkingRepository.findById(id);
    ParkingSpace parkingSpace = parkingSpaceRepository.findById(completedParking.getParkingSpaceId());

    parkingSpace.setStatus(Status.AVAILABLE);
    parkingSpace.setCurrentParkingInformation(null);
    parkingSpaceRepository.save(parkingSpace);
    nextAvailableParkingSpaceCache.put(parkingSpace.getType(), parkingSpace.getId());

    completedParking.setEndDateTime(LocalDateTime.now());
    long parkingDurationSeconds = SECONDS.between(completedParking.getStartDateTime(), completedParking.getEndDateTime());
    completedParking.setDurationSeconds(parkingDurationSeconds);
    completedParking.setBilling(calculateBilling(completedParking));

    return parkingRepository.save(completedParking);
  }

  private void updateNextAvailableCache(ParkingSpace.Type type) {
    List<ParkingSpace> availableParkingSpaces = parkingSpaceRepository.findByTypeAndStatus(type, Status.AVAILABLE);
    if (!CollectionUtils.isEmpty(availableParkingSpaces)) {
      nextAvailableParkingSpaceCache.put(type, availableParkingSpaces.get(0).getId());
    } else {
      nextAvailableParkingSpaceCache.put(type, null);
    }
  }

  @SneakyThrows
  private Billing calculateBilling(Parking parking) {
    ProducerRecord<String, Parking> record =
      new ProducerRecord<>(requestTopics.getBillingCalculation(), null, UUID.randomUUID().toString(), parking);
    RequestReplyFuture<String, Parking, Billing> future = billingCalculationKafkaTemplate.sendAndReceive(record);
    return future.get().value();
  }

}
