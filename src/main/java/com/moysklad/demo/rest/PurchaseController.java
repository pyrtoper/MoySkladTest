package com.moysklad.demo.rest;

import com.moysklad.demo.entity.Purchase;
import com.moysklad.demo.handler.ApiError;
import com.moysklad.demo.service.impl.PurchaseService;
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
@RequestMapping(value = "/purchases")
public class PurchaseController {

  private final PurchaseService purchaseService;

  public PurchaseController(PurchaseService purchaseService) {
    this.purchaseService = purchaseService;
  }

  @Operation(
      tags = "Purchases",
      description = "Get all purchases",
      responses = {@ApiResponse(responseCode = "200",
          description = "Returns the list of purchases")}
  )
  @GetMapping(produces = "application/json")
  public List<Purchase> getPurchases() {
    return purchaseService.getPurchases();
  }
  @Operation(
      tags = "Purchases",
      description = "Get purchase by id",
      responses = {@ApiResponse(responseCode = "200",
          description = "Return the requested purchase",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = Purchase.class))),
          @ApiResponse(responseCode = "400",
              description = "Purchase with provided id does not exist",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = Purchase.class)))}
  )
  @GetMapping("/{purchaseId}")
  public Purchase getPurchase(@PathVariable Long purchaseId) {
    return purchaseService.findById(purchaseId).orElseThrow(
        () -> new EntityNotFoundException("Purchase with provided id does not exist")
    );
  }

  @Operation(
      tags = "Purchases",
      description = "Creates a purchase. Adds provided products in requested storehouse and "
          + "updates lastPurchasePrice in corresponding products",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = Purchase.class))
      ),
      responses = {
          @ApiResponse(responseCode = "200",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = Purchase.class))),
          @ApiResponse(responseCode = "400",
              description = "Invalid purchase parameters, details in subErrors",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiError.class))
          )}
  )
  @PostMapping(consumes = "application/json", produces = "application/json")
  public Purchase addPurchase(@Valid @RequestBody Purchase purchase) {
    return purchaseService.process(purchase);
  }
  @Operation(
      tags = "Purchases",
      description = "Update a purchase. Warning: that operation will not do anything in a database "
          + "with storehouses or products",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = Purchase.class))
      ),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Purchase was updated",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = Purchase.class))),
          @ApiResponse(responseCode = "400",
              description = "Invalid purchase parameters, details in subErrors",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiError.class))
          )}
  )
  @PutMapping(consumes = "application/json", produces = "application/json")
  public Purchase updatePurchase(@Valid @RequestBody Purchase purchase) {
    purchaseService.findById(purchase.getId()).orElseThrow(
        () -> new EntityNotFoundException("Purchase with that id does not exist")
    );
    return purchaseService.save(purchase);
  }

  @Operation(
      tags = "Purchases",
      description = "Delete a purchase. Warning: that operation will not do anything in a database "
          + "with storehouses or products",
      parameters = @Parameter(name = "purchaseId", description = "Id of a purchase"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Purchase was deleted"),
          @ApiResponse(responseCode = "400",
              description = "Invalid purchase id"
          )}
  )
  @DeleteMapping("/{purchaseId}")
  public void deletePurchase(@PathVariable("purchaseId") Long purchaseId) {
    if (purchaseService.findById(purchaseId).isEmpty()) {
      throw new EntityNotFoundException("Purchase with that id does not exist");
    }
    purchaseService.deleteById(purchaseId);
  }

  @Operation(
      tags = "Purchases",
      description = "Delete all purchases. Warning: that operation will not do anything in a database "
          + "with storehouses or products",
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Purchases were deleted")
          }
  )
  @DeleteMapping()
  public void deleteAllPurchases () {
    purchaseService.deleteAll();
  }
}
