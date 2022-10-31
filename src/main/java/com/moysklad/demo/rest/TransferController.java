package com.moysklad.demo.rest;

import com.moysklad.demo.entity.Transfer;
import com.moysklad.demo.handler.ApiError;
import com.moysklad.demo.service.impl.TransferFromToService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import javax.persistence.EntityNotFoundException;
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
@RequestMapping(value = "/transfers")
public class TransferController {

  private final TransferFromToService transferFromToService;

  public TransferController(TransferFromToService transferFromToService) {
    this.transferFromToService = transferFromToService;
  }

  @Operation(
      tags = "Transfers",
      description = "Get all transfers",
      responses = {@ApiResponse(responseCode = "200",
          description = "Returns the list of transfers")}
  )
  @GetMapping(produces = "application/json")
  public List<Transfer> getTransfers() {
    return transferFromToService.getTransfers();
  }

  @Operation(
      tags = "Transfers",
      description = "Get transfer by id",
      responses = {@ApiResponse(responseCode = "200",
          description = "Return the requested transfer",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = Transfer.class))),
          @ApiResponse(responseCode = "400",
              description = "Transfer with that id does not exist",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = ApiError.class)))}
  )
  @GetMapping("/{transferId}")
  public Transfer getTransfer(@PathVariable Long transferId) {
    return transferFromToService.findById(transferId).orElseThrow(
        () -> new EntityNotFoundException("Transfer with that id does not exist")
    );
  }

  @Operation(
      tags = "Transfers",
      description = "Create a transfer. Transfers corresponding products from one storehouse to "
          + "another and if it was successful, saves transfer document to database",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = Transfer.class))
      ),
      responses = {
          @ApiResponse(responseCode = "200",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = Transfer.class))),
          @ApiResponse(responseCode = "400",
              description = "Invalid transfer parameters, details in subErrors",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiError.class))
          )}
  )
  @PostMapping(consumes = "application/json", produces = "application/json")
  public Transfer addPurchase(@Valid @RequestBody Transfer transfer) {
    return transferFromToService.process(transfer);
  }

  @Operation(
      tags = "Transfers",
      description = "Update a transfer. Warning: that operation will not do anything in a database "
          + "with storehouses or products",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = Transfer.class))
      ),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Transfer was updated",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = Transfer.class))),
          @ApiResponse(responseCode = "400",
              description = "Invalid transfer parameters, details in subErrors",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiError.class))
          )}
  )
  @PutMapping(consumes = "application/json", produces = "application/json")
  public Transfer updatePurchase(@Valid @RequestBody Transfer transfer) {
    transferFromToService.findById(transfer.getId()).orElseThrow(
        () -> new EntityNotFoundException("Transfer with that id does not exist")
    );
    return transferFromToService.save(transfer);
  }

  @Operation(
      tags = "Transfers",
      description = "Delete a transfer. Warning: that operation will not do anything in a database "
          + "with storehouses or products",
      parameters = @Parameter(name = "transferId", description = "Id of a transfer"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Transfer was deleted"),
          @ApiResponse(responseCode = "400",
              description = "Invalid transfer id"
          )}
  )
  @DeleteMapping("/{transferId}")
  public void deleteSale(@PathVariable("transferId") Long transferId) {
    if (transferFromToService.findById(transferId).isEmpty()) {
      throw new EntityNotFoundException("Transfer with that id does not exist");
    }
    transferFromToService.deleteById(transferId);
  }

  @Operation(
      tags = "Transfers",
      description = "Delete all transfers. Warning: that operation will not do anything in a database "
          + "with storehouses or products",
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Transfers were deleted")
      }
  )
  @DeleteMapping()
  public void deleteAllSales () {
    transferFromToService.deleteAll();
  }
}
