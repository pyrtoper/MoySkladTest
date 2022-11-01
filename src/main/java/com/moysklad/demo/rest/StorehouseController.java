package com.moysklad.demo.rest;

import com.moysklad.demo.entity.Storehouse;
import com.moysklad.demo.exception.StorehouseDoesNotExistException;
import com.moysklad.demo.handler.ApiError;
import com.moysklad.demo.repository.StorehouseRepository;
import com.moysklad.demo.service.StorehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import javax.persistence.EntityExistsException;
import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storehouses")
public class StorehouseController {

  private final StorehouseRepository storehouseRepository;
  private final StorehouseService storehouseService;

  public StorehouseController(StorehouseRepository storehouseRepository,
      StorehouseService storehouseService) {
    this.storehouseRepository = storehouseRepository;
    this.storehouseService = storehouseService;
  }

  @Operation(
      tags = "Storehouses",
      description = "Get all storehouses",
      responses = {@ApiResponse(responseCode = "200",
          description = "Returns the list of storehouses")}
  )
  @GetMapping(produces = "application/json")
  public List<Storehouse> getStorehouses() {
    return storehouseRepository.findAll();
  }
  @Operation(
      tags = "Storehouses",
      description = "Get a storehouse by id",
      responses = {@ApiResponse(responseCode = "200",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = Storehouse.class)),
          description = "Returns a storehouse"),
      @ApiResponse(responseCode = "400",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = ApiError.class)),
          description = "Storehouse with provided id wasn't found")}
  )
  @GetMapping(value = "/{storehouseId}")
  public Storehouse getStorehouse(@PathVariable("storehouseId") Long storehouseId) {
    return storehouseRepository.findById(storehouseId).orElseThrow(
        StorehouseDoesNotExistException::new
    );
  }

  @Operation(
      tags = "Storehouses",
      description = "Create a storehouse",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = Storehouse.class))
      ),
      responses = {
          @ApiResponse(responseCode = "200",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = Storehouse.class))),
          @ApiResponse(responseCode = "400",
              description = "Invalid storehouse, details in subErrors",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiError.class))
          )}
  )
  @PostMapping(
      produces = "application/json", consumes = "application/json")
  public Storehouse createStorehouse(@RequestBody @Valid Storehouse storehouse) {
    if (storehouse.getId() == null || storehouseRepository.findById(storehouse.getId()).isEmpty()) {
      storehouseRepository.save(storehouse);
    } else {
      throw new EntityExistsException("Storehouse with that id already exists");
    }
    return storehouse;
  }


  @Operation(
      tags = "Storehouses",
      description = "Update a storehouse",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = Storehouse.class))
      ),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Storehouse was updated",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = Storehouse.class))),
          @ApiResponse(responseCode = "400",
              description = "Storehouse with provided id doesn't exist",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiError.class))
          )}
  )
  @PutMapping(
      consumes = "application/json", produces = "application/json"
  )
  public Storehouse updateStorehouse(@RequestBody @Valid Storehouse storehouse) {
    if (storehouseRepository.findById(storehouse.getId()).isEmpty()) {
      throw new StorehouseDoesNotExistException();
    }
    storehouseRepository.save(storehouse);
    return storehouse;
  }

  @Operation(
      tags = "Storehouses",
      description = "Delete a storehouse",
      parameters = @Parameter(name = "storehouseId", description = "Id of a storehouse"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Storehouse was deleted"),
          @ApiResponse(responseCode = "400",
              description = "Storehouse with provided id wasn't found"
          )}
  )
  @DeleteMapping(value = "/{storehouseId}",
      produces = "application/json")
  public void deleteStorehouse(@PathVariable Long storehouseId) {
    storehouseService.deleteById(storehouseId);
  }
  @Operation(
      tags = "Storehouses",
      description = "Delete all storehouses",
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Storehouses were deleted")
      }
  )
  @DeleteMapping
  public void deleteStorehouse() {
    storehouseService.deleteAll();
  }
}
