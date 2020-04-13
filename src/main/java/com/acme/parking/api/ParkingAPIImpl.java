package com.acme.parking.api;

import com.acme.parking.exception.InvalidParkingIdException;
import com.acme.parking.exception.InvalidParkingRequestException;
import com.acme.parking.exception.InvalidParkingSpaceIdException;
import com.acme.parking.exception.InvalidParkingSpaceStatusException;
import com.acme.parking.exception.ParkingAlreadyCompletedException;
import com.acme.parking.inventory.model.Parking;
import com.acme.parking.inventory.model.ParkingSpace;
import com.acme.parking.properties.KafkaRequestTopics;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class ParkingAPIImpl implements ParkingAPI {

  private final KafkaRequestTopics requestTopics;

  private final ReplyingKafkaTemplate<String, Long, Parking> parkingByIdKafkaTemplate;
  private final ReplyingKafkaTemplate<String, Long, ParkingSpace> parkingSpaceByIdKafkaTemplate;
  private final ReplyingKafkaTemplate<String, Parking, Parking> parkingStartKafkaTemplate;
  private final ReplyingKafkaTemplate<String, Long, Parking> parkingCompletionKafkaTemplate;

  @Autowired
  public ParkingAPIImpl(KafkaRequestTopics requestTopics,
                        @Qualifier("parkingById") ReplyingKafkaTemplate<String, Long, Parking> parkingByIdKafkaTemplate,
                        ReplyingKafkaTemplate<String, Long, ParkingSpace> parkingSpaceByIdKafkaTemplate,
                        ReplyingKafkaTemplate<String, Parking, Parking> parkingStartKafkaTemplate,
                        @Qualifier("parkingCompletion") ReplyingKafkaTemplate<String, Long, Parking> parkingCompletionKafkaTemplate) {
    this.requestTopics = requestTopics;
    this.parkingByIdKafkaTemplate = parkingByIdKafkaTemplate;
    this.parkingSpaceByIdKafkaTemplate = parkingSpaceByIdKafkaTemplate;
    this.parkingStartKafkaTemplate = parkingStartKafkaTemplate;
    this.parkingCompletionKafkaTemplate = parkingCompletionKafkaTemplate;
  }

  @SneakyThrows
  @Override
  public ResponseEntity<List<Parking>> startParking(@Valid Parking parkingRequested) {
    validateCreateRequest(parkingRequested);

    ProducerRecord<String, Parking> record =
      new ProducerRecord<>(requestTopics.getParkingStart(), null, UUID.randomUUID().toString(), parkingRequested);
    RequestReplyFuture<String, Parking, Parking> future = parkingStartKafkaTemplate.sendAndReceive(record);
    Parking parkingCreated = future.get().value();
    return ResponseEntity.created(URI.create("/parking/" + parkingCreated.getId())).build();
  }

  @SneakyThrows
  @Override
  public ResponseEntity<Parking> getParkingInformation(Long id) {
    Parking ongoingParking = getParking(id);
    return ResponseEntity.ok(ongoingParking);
  }

  @SneakyThrows
  @Override
  public ResponseEntity<Parking> endParking(Long id) {
    Parking parking = getParking(id);
    if (parking.getBilling() != null) {
      throw new ParkingAlreadyCompletedException(id);
    }
    ProducerRecord<String, Long> record =
      new ProducerRecord<>(requestTopics.getParkingCompletion(), null, UUID.randomUUID().toString(), id);
    RequestReplyFuture<String, Long, Parking> future = parkingCompletionKafkaTemplate.sendAndReceive(record);
    Parking completedParking = future.get().value();

    return ResponseEntity.ok(completedParking);
  }


  private Parking getParking(Long id) throws InterruptedException, java.util.concurrent.ExecutionException {
    ProducerRecord<String, Long> record =
      new ProducerRecord<>(requestTopics.getParkingById(), null, UUID.randomUUID().toString(), id);
    RequestReplyFuture<String, Long, Parking> future = parkingByIdKafkaTemplate.sendAndReceive(record);
    Parking parking = future.get().value();
    if (parking.getId() == null) {
      throw new InvalidParkingIdException(id);
    }
    return parking;
  }

  @SneakyThrows
  private void validateCreateRequest(Parking parking) {
    if (parking.getParkingSpaceId() == null) {
      throw new InvalidParkingRequestException("parkingSpaceId is required");
    }
    if (parking.getEndDateTime() != null || parking.getDurationSeconds() != null || parking.getBilling() != null) {
      throw new InvalidParkingRequestException("Please provide only the parkingSpaceId");
    }
    ParkingSpace parkingSpace = getParkingSpace(parking.getParkingSpaceId());
    if (parkingSpace.getStatus() != ParkingSpace.Status.AVAILABLE) {
      throw new InvalidParkingSpaceStatusException(parking.getParkingSpaceId(), parkingSpace.getStatus().name());
    }
  }

  private ParkingSpace getParkingSpace(Long id) throws InterruptedException, java.util.concurrent.ExecutionException {
    ProducerRecord<String, Long> record = new ProducerRecord<>(requestTopics.getParkingSpaceById(),
      null, UUID.randomUUID().toString(), id);
    RequestReplyFuture<String, Long, ParkingSpace> future = parkingSpaceByIdKafkaTemplate.sendAndReceive(record);
    ParkingSpace parkingSpaceFound = future.get().value();
    if (parkingSpaceFound.getId() == null) {
      throw new InvalidParkingSpaceIdException(id);
    }
    return parkingSpaceFound;
  }
}