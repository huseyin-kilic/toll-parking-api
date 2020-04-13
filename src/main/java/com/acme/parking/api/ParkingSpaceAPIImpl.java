package com.acme.parking.api;

import com.acme.parking.api.model.ParkingSpaceQuery;
import com.acme.parking.api.model.ParkingSpaceQueryResult;
import com.acme.parking.exception.InvalidParkingSpaceIdException;
import com.acme.parking.inventory.model.ParkingSpace;
import com.acme.parking.inventory.model.ParkingSpace.Status;
import com.acme.parking.inventory.model.ParkingSpace.Type;
import com.acme.parking.properties.KafkaRequestTopics;
import lombok.SneakyThrows;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@RestController
public class ParkingSpaceAPIImpl implements ParkingSpaceAPI {

  private final KafkaRequestTopics requestTopics;

  private final ReplyingKafkaTemplate<String, Long, ParkingSpace> parkingSpaceByIdKafkaTemplate;

  private final ReplyingKafkaTemplate<String, Type, ParkingSpace> nextAvailableParkingSpaceKafkaTemplate;

  private final ReplyingKafkaTemplate<String, ParkingSpaceQuery, ParkingSpaceQueryResult> parkingSpacesQueryKafkaTemplate;

  @Autowired
  public ParkingSpaceAPIImpl(ReplyingKafkaTemplate<String, Long, ParkingSpace> parkingSpaceByIdKafkaTemplate,
                             ReplyingKafkaTemplate<String, Type, ParkingSpace> nextAvailableParkingSpaceKafkaTemplate,
                             ReplyingKafkaTemplate<String, ParkingSpaceQuery, ParkingSpaceQueryResult> parkingSpacesQueryKafkaTemplate,
                             KafkaRequestTopics requestTopics) {
    this.parkingSpaceByIdKafkaTemplate = parkingSpaceByIdKafkaTemplate;
    this.nextAvailableParkingSpaceKafkaTemplate = nextAvailableParkingSpaceKafkaTemplate;
    this.parkingSpacesQueryKafkaTemplate = parkingSpacesQueryKafkaTemplate;
    this.requestTopics = requestTopics;
  }

  @Override
  @SneakyThrows
  public ResponseEntity<ParkingSpace> getParkingSpaceById(Long id) {
    ParkingSpace parkingSpaceFound = getParkingSpace(id);
    return ResponseEntity.ok(parkingSpaceFound);
  }

  private ParkingSpace getParkingSpace(Long id) throws InterruptedException, java.util.concurrent.ExecutionException {
    ProducerRecord<String, Long> record = new ProducerRecord<>(requestTopics.getParkingSpaceById(), null, UUID.randomUUID().toString(), id);
    RequestReplyFuture<String, Long, ParkingSpace> future = parkingSpaceByIdKafkaTemplate.sendAndReceive(record);
    ParkingSpace parkingSpaceFound = future.get().value();
    if (parkingSpaceFound.getId() == null) {
      throw new InvalidParkingSpaceIdException(id);
    }
    return parkingSpaceFound;
  }

  @Override
  @SneakyThrows
  public ResponseEntity<List<ParkingSpace>> searchParkingSpaces(Type type, Status status, Integer count) {
    if (count == 1 & status == Status.AVAILABLE) {
      ParkingSpace nextAvailableParkingSpace = getNextAvailableParkingSpace(type);
      if (nextAvailableParkingSpace.getId() != null) {
        return ResponseEntity.ok(List.of(nextAvailableParkingSpace));
      } else {
        return ResponseEntity.noContent().build();
      }
    } else {
      List<ParkingSpace> matchingParkingSpaces = queryParkingSpaces(type, status, count);
      if (CollectionUtils.isEmpty(matchingParkingSpaces)) {
        return ResponseEntity.noContent().build();
      } else {
        return ResponseEntity.ok(matchingParkingSpaces);
      }
    }
  }

  private List<ParkingSpace> queryParkingSpaces(Type type, Status status, Integer count) throws InterruptedException, ExecutionException {
    ProducerRecord<String, ParkingSpaceQuery> record =
      new ProducerRecord<>(requestTopics.getParkingSpacesQuery(), null, UUID.randomUUID().toString(), new ParkingSpaceQuery(type, status, count));
    RequestReplyFuture<String, ParkingSpaceQuery, ParkingSpaceQueryResult> future = parkingSpacesQueryKafkaTemplate.sendAndReceive(record);
    return future.get().value().getResult();
  }

  private ParkingSpace getNextAvailableParkingSpace(Type type) throws InterruptedException, ExecutionException {
    ProducerRecord<String, Type> record =
      new ProducerRecord<>(requestTopics.getNextAvailableParkingSpace(), null, UUID.randomUUID().toString(), type);
    RequestReplyFuture<String, Type, ParkingSpace> future = nextAvailableParkingSpaceKafkaTemplate.sendAndReceive(record);
    return future.get().value();
  }

}
