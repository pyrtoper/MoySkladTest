package com.moysklad.demo.rest;

import com.moysklad.demo.entity.Product;
import com.moysklad.demo.exception.ProductDoesNotExist;
import com.moysklad.demo.handler.ApiError;
import com.moysklad.demo.repository.ProductRepository;
import com.moysklad.demo.service.ProductService;
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
@RequestMapping("/products")
public class ProductController {

  private final ProductRepository productRepository;
  private final ProductService productService;

  public ProductController(ProductRepository productRepository,
      ProductService productService) {
    this.productRepository = productRepository;
    this.productService = productService;
  }

  @Operation(
      tags = "Products",
      description = "Get all products",
      responses = {@ApiResponse(responseCode = "200",
          description = "Returns the list of products")}
  )
  @GetMapping(produces = "application/json")
  public List<Product> getProducts() {
    return productRepository.findAll();
  }

  @Operation(
      tags = "Products",
      description = "Get product by vendor code",
      responses = {@ApiResponse(responseCode = "200",
          description = "Return the requested product"),
          @ApiResponse(responseCode = "400",
          description = "Product with provided vendor code wasn't found")}
  )
  @GetMapping(value = "/{vendorCode}", produces = "application/json")
  public Product getProduct(@PathVariable("vendorCode") String vendorCode) {
    return productRepository.findByVendorCode(vendorCode).orElseThrow(
        ProductDoesNotExist::new
    );
  }

  @Operation(
      tags = "Products",
      description = "Create a product",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = Product.class))
      ),
      responses = {
          @ApiResponse(responseCode = "200",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
              schema = @Schema(implementation = Product.class))),
          @ApiResponse(responseCode = "400",
              description = "Invalid product parameters, details in subErrors",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiError.class))
          )}
  )
  @PostMapping(consumes = "application/json", produces = "application/json")
  public Product addProduct(@Valid @RequestBody Product product) {
    if (productRepository.findByVendorCode(product.getVendorCode()).isPresent()) {
      throw new EntityExistsException("Product with such vendor code already exists");
    }
    return productRepository.save(product);
  }

  @Operation(
      tags = "Products",
      description = "Update a product",
      requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
          content = @Content(schema = @Schema(implementation = Product.class))
      ),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Product was updated",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
          @ApiResponse(responseCode = "400",
              description = "Invalid product parameters, details in subErrors",
              content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                  schema = @Schema(implementation = ApiError.class))
          )}
  )
  @PutMapping(consumes = "application/json", produces = "application/json")
  public Product updateProduct(@Valid @RequestBody Product product) {
    Product existingProduct = productRepository.findByVendorCode(product.getVendorCode())
        .orElseThrow(() -> new EntityExistsException("Product with such vendor code does not exist"));
    existingProduct.setName(product.getName());
    existingProduct.setLastSalePrice(product.getLastSalePrice());
    existingProduct.setLastPurchasePrice(product.getLastPurchasePrice());
    return productRepository.save(existingProduct);
  }


  @Operation(
      tags = "Products",
      description = "Delete a product",
      parameters = @Parameter(name = "vendorCode", description = "Vendor code of a product"),
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Product was deleted"),
          @ApiResponse(responseCode = "400",
              description = "Product with provided vendor code wasn't found"
          )}
  )
  @DeleteMapping("/{vendorCode}")
  public void deleteProduct(@PathVariable("vendorCode") String vendorCode) {
    productService.deleteByVendorCode(vendorCode);
  }

  @Operation(
      tags = "Products",
      description = "Delete all products",
      responses = {
          @ApiResponse(responseCode = "200",
              description = "Products were deleted")
      }
  )
  @DeleteMapping()
  public void deleteAllProducts () {
      productService.deleteAll();
  }
}
