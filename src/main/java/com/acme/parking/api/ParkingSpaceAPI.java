package com.acme.parking.api;

import com.acme.parking.inventory.model.ParkingSpace;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/parking-spaces", produces = {"application/json"})
@Validated
@Tag(name = "Parking Space Actions",
  description = "Exposes the current state of the parking area. Available actions: " +
    "\n 1) Retrieve current state of a parking space by id." +
    "\n 2) Retrieve next available parking space for a given car type." +
    "\n 3) Retrieve list of parking spaces matching given search criteria.")
public interface ParkingSpaceAPI {

  @Operation(summary = "Finds parking space by id", description = "Parking space retrieval by id", tags = {"Parking Space"})
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = ParkingSpace.class)))),
    @ApiResponse(responseCode = "400", description = "Malformed parking space id",
      content = @Content(schema = @Schema())),
    @ApiResponse(responseCode = "404", description = "No parking space exists with given id",
      content = @Content(schema = @Schema())),
    @ApiResponse(responseCode = "500", description = "Server error",
      content = @Content(schema = @Schema()))
  })
  @GetMapping(path = "/{id}")
  ResponseEntity<ParkingSpace> getParkingSpaceById(@Parameter(description = "Id of the parking space to retrieve", required = true)
                                                   @PathVariable Long id);


  @Operation(summary = "Lists parking spaces matching given criteria",
    description = "Can be used in order to: " +
      "\n 1) Retrieve the next available parking space for given car type (if 'type' is the only query parameter provided)" +
      "\n 2) Retrieve the list of parking spaces matching given criteria", tags = {"Parking Space"})
  @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Successful operation",
      content = @Content(array = @ArraySchema(schema = @Schema(implementation = ParkingSpace.class)))),
    @ApiResponse(responseCode = "204", description = "No parking space available with given criteria",
      content = @Content(schema = @Schema())),
    @ApiResponse(responseCode = "400", description = "Invalid query arguments",
      content = @Content(schema = @Schema())),
    @ApiResponse(responseCode = "404", description = "HTTP-404 is never returned for this request. " +
      "\nIn case no parking spaces found, HTTP-204 is returned as shown above.",
      content = @Content(schema = @Schema())),
    @ApiResponse(responseCode = "500", description = "Server error",
      content = @Content(schema = @Schema()))
  })
  @GetMapping
  ResponseEntity<List<ParkingSpace>> searchParkingSpaces(@Parameter(description = "Car type", required = true)
                                                         @RequestParam("type") ParkingSpace.Type type,
                                                         @Parameter(description = "Current status of the parking space")
                                                         @RequestParam(value = "status", required = false, defaultValue = "AVAILABLE") ParkingSpace.Status status,
                                                         @Parameter(description = "Number of parking spaces to retrieve")
                                                         @RequestParam(value = "count", required = false, defaultValue = "1") @Min(1) Integer count);

}
