package com.acme.parking;

import com.acme.parking.properties.AppProperties;
import com.acme.parking.inventory.dao.ParkingSpaceRepository;
import com.acme.parking.inventory.model.ParkingSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.stream.LongStream;

@SpringBootApplication
public class TollParkingApplication {

  public static Logger log = LoggerFactory.getLogger(TollParkingApplication.class);

  @Autowired
  private AppProperties config;

  public static void main(String[] args) {
    SpringApplication.run(TollParkingApplication.class, args);
  }

  @Bean
  public CommandLineRunner initializeParkingSpaces(ParkingSpaceRepository repository) {
    log.info("Initializing parking spaces...");
    return (args) -> {
      log.info(String.format("Creating %d parking spaces for type GASOLINE", config.getTypeGasolineCount()));
      LongStream.rangeClosed(1, config.getTypeGasolineCount()).forEach(i ->
        repository.save(new ParkingSpace(i, ParkingSpace.Type.GASOLINE)));

      log.info(String.format("Creating %d parking spaces for type KW20", config.getTypeKW20Count()));
      LongStream.rangeClosed(config.getTypeGasolineCount() + 1,
        config.getTypeGasolineCount() + config.getTypeKW20Count())
        .forEach(i -> repository.save(new ParkingSpace(i, ParkingSpace.Type.KW20)));

      log.info(String.format("Creating %d parking spaces for type KW50", config.getTypeKW50Count()));
      LongStream.rangeClosed(config.getTypeGasolineCount() + config.getTypeKW20Count() + 1,
        config.getTypeGasolineCount() + config.getTypeKW20Count() + config.getTypeKW50Count())
        .forEach(i -> repository.save(new ParkingSpace(i, ParkingSpace.Type.KW50)));
    };
  }
}