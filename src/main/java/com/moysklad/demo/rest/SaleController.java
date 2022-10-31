package com.moysklad.demo.rest;

import com.moysklad.demo.entity.Sale;
import com.moysklad.demo.handler.ApiError;
import com.moysklad.demo.service.impl.SaleService;
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
@RequestMapping("/sales")
public class SaleController {
  private final SaleService saleService;

  public SaleController(SaleService saleService) {
    this.saleService = saleService;
  }

  @Operation(
      tags = "Sales",
      description = "Get all sales",
      responses = {@ApiResponse(responseCode = "200",
          description = "Returns the list of sales")}
  )
  @GetMapping(produces = "application/json")
  public List<Sale> getSales() {
    return saleService.getSales();
  }
  @Operation(
      tags = "Sales",
      description = "Get sale by id",
      responses = {@ApiResponse(responseCode = "200",
          description = "Return the requested sale",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = Sale.class))),
              @ApiResponse(responseCode = "400",
                  description = "Sale with that id does not exist",
                  content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiError.class)))}
  )
  @GetMapping("/{saleId}")
  public Sale getSale(@PathVariable Long saleId) {
    return saleService.findById(saleId).orElseThrow(
        () -> new EntityNotFoundException("Sale with that id does not exist")
    );
  }
  @Operation(
      tags = "Sales",
      description = "Create a sale. Removes provided products from a storehouse with provided id, "
          + "updates lastSalePrice in corresponding products",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = Sale.class))
      ),
      responses = {
          @ApiResponse(responseCode = "200",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = Sale.class))),
          @ApiResponse(responseCode = "400",
              description = "Invalid sale parameters, details in subErrors",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiError.class))
          )}
  )
  @PostMapping(consumes = "application/json", produces = "application/json")
  public Sale addSale(@Valid @RequestBody Sale sale) {
    return saleService.process(sale);
  }

  @Operation(
      tags = "Sales",
      description = "Update a sale. Warning: that operation will not do anything in a database "
          + "with storehouses or products",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = Sale.class))
      ),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Sale was updated",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = Sale.class))),
          @ApiResponse(responseCode = "400",
              description = "Invalid sale parameters, details in subErrors",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiError.class))
          )}
  )
  @PutMapping(consumes = "application/json", produces = "application/json")
  public Sale updatePurchase(@Valid @RequestBody Sale sale) {
    saleService.findById(sale.getId()).orElseThrow(
        () -> new EntityNotFoundException("Sale with that id does not exist")
    );
    return saleService.save(sale);
  }

  @Operation(
      tags = "Sales",
      description = "Delete a sale. Warning: that operation will not do anything in a database "
          + "with storehouses or products",
      parameters = @Parameter(name = "saleId", description = "Id of a sale"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Sale was deleted"),
          @ApiResponse(responseCode = "400",
              description = "Invalid sale id"
          )}
  )
  @DeleteMapping("/{saleId}")
  public void deleteSale(@PathVariable("saleId") Long saleId) {
    if (saleService.findById(saleId).isEmpty()) {
      throw new EntityNotFoundException("Sale with that id does not exist");
    }
    saleService.deleteById(saleId);
  }

  @Operation(
      tags = "Sales",
      description = "Delete all sales. Warning: that operation will not do anything in a database "
          + "with storehouses or products",
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Sales were deleted")
      }
  )
  @DeleteMapping()
  public void deleteAllSales () {
    saleService.deleteAll();
  }
}
