package com.moysklad.demo.rest;

import com.moysklad.demo.dto.ProductGeneralInfoDTO;
import com.moysklad.demo.entity.Product;
import com.moysklad.demo.handler.ApiError;
import com.moysklad.demo.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
public class ReportController {

  private final ReportService reportService;

  public ReportController(ReportService reportService) {
    this.reportService = reportService;
  }


  @Operation(
      tags = "Reports",
      parameters = @Parameter(name = "productName",
          description = "Optional parameter"),
      description = "If productName not specified: returns product info of all products. Otherwise, "
          + "returns info about products with provided name. If provided name wasn't found: returns "
          + "as empty response",
      responses = {@ApiResponse(responseCode = "200",
          description = "Return the requested products info"),
      }
  )
  @GetMapping(value = "/products",
      produces = "application/json")
  public List<Product> getProductsReport(@RequestParam(value = "productName", required = false) String productName) {
    if (productName == null) {
      return reportService.getAllProductsInfo();
    }
    return reportService.getProductInfo(productName);
  }

  @Operation(
      tags = "Reports",
      parameters = @Parameter(name = "storehouseId",
          description = "Optional parameter"),
      description = "If storehouseId not specified: returns products remainders in all storehouses. "
          + "Otherwise,returns products remainders in a storehouse with provided id. "
          + "StorehouseId : productsSet",
      responses = {@ApiResponse(responseCode = "200",
          description = "Return the requested products info"),
      @ApiResponse(responseCode = "400",
          description = "Storehouse with provided id does not exist",
          content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
          schema = @Schema(implementation = ApiError.class)))}
  )
  @GetMapping(value = "/remainders",
      produces = "application/json")
  public Map<Long, List<ProductGeneralInfoDTO>> getRemaindersReport(
      @RequestParam(value = "storehouseId", required = false) Long storehouseId) {
    if (storehouseId == null) {
      return reportService.getAllRemainders();
    }
    return reportService.getStorehouseRemaindersInfo(storehouseId);
  }
}
