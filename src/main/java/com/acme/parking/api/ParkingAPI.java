package com.acme.parking.api;

import com.acme.parking.inventory.model.Parking;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/v1/parkings", produces = {"application/json"})
@Validated
@Tag(name = "Parking Actions",
  description = "Exposes operations around actual parking. Available actions: " +
    "\n 1) Start a new parking at a given parking lot." +
    "\n 2) Complete an existing parking & return billing information." +
    "\n 3) Retrieve details about an existing parking.")
public interface ParkingAPI {

  @Operation(summary = "Starts a new parking at the provided parking space",
    description = "Starts a new parking at the given parking space & marks the parking space as occupied." +
      "\nStart time is set automatically by the system", tags = {"Parking"})
  @ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = Parking.class))),
    @ApiResponse(responseCode = "400", description = "Bad request (Invalid arguments or Parking space is already occupied)",
      content = @Content(schema = @Schema())),
    @ApiResponse(responseCode = "404", description = "No parking space exists with given id",
      content = @Content(schema = @Schema())),
    @ApiResponse(responseCode = "500", description = "Server error",
      content = @Content(schema = @Schema())),
  })
  @PostMapping
  ResponseEntity<List<Parking>> startParking(@Valid
                                             @Parameter(description = "Start parking at the given parking space. " +
                                               "\nOnly the parking space id should be provided.", required = true)
                                             @RequestBody Parking parking);


  @Operation(summary = "Returns parking information by id", description = "Parking information retrieval by id", tags = {"Parking"})
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = Parking.class))),
    @ApiResponse(responseCode = "400", description = "Malformed parking id",
      content = @Content(schema = @Schema())),
    @ApiResponse(responseCode = "404", description = "No parking information exists with given id",
      content = @Content(schema = @Schema())),
    @ApiResponse(responseCode = "500", description = "Server error",
      content = @Content(schema = @Schema())),
  })
  @GetMapping(path = "/{id}")
  ResponseEntity<Parking> getParkingInformation(@Parameter(description = "Id of the parking information to retrieve", required = true)
                                                @PathVariable Long id);


  @Operation(summary = "Ends an existing parking",
    description = "Completes the parking with the given parking id & marks the parking space as available." +
      "\nEnd time is set automatically by the system." +
      "\nDuration and billing information are calculated by the system.", tags = {"Parking"})
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(schema = @Schema(implementation = Parking.class))),
    @ApiResponse(responseCode = "400", description = "Trying to end a parking that is already completed",
      content = @Content(schema = @Schema())),
    @ApiResponse(responseCode = "404", description = "No parking information exists with given id",
      content = @Content(schema = @Schema())),
    @ApiResponse(responseCode = "500", description = "Server error",
      content = @Content(schema = @Schema())),
  })
  @DeleteMapping(path = "/{id}")
  ResponseEntity<Parking> endParking(@Parameter(description = "Id of the parking to complete", required = true)
                                     @PathVariable Long id);

}
