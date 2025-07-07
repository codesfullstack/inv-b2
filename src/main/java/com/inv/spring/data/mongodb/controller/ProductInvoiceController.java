package com.inv.spring.data.mongodb.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inv.spring.data.mongodb.model.ProductInvoice;
import com.inv.spring.data.mongodb.repository.ProductInvoiceRepository;
import org.springframework.dao.DataIntegrityViolationException; // Importa esta excepciÃ³n

@CrossOrigin(origins = "http://localhost:3000", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
    RequestMethod.DELETE, RequestMethod.OPTIONS })
@RestController
@RequestMapping("/api/products-invoices")
public class ProductInvoiceController {
  @Autowired
  private ProductInvoiceRepository productInvoiceRepository;

  @PostMapping("/add-product-invoice")
  public ResponseEntity<?> addProductInvoice(@RequestBody ProductInvoice productInvoice) {
    try {
      ProductInvoice newProductInvoice = new ProductInvoice();
      newProductInvoice.setAmount(productInvoice.getAmount());
      newProductInvoice.setDateIssue(productInvoice.getDateIssue());
      newProductInvoice.setDescription(productInvoice.getDescription());
      newProductInvoice.setIdUsuario(productInvoice.getIdUsuario());
      newProductInvoice.setInvoiceID(productInvoice.getInvoiceID());
      newProductInvoice.setInvoiceType(productInvoice.getInvoiceType());
      newProductInvoice.setName(productInvoice.getName());
      newProductInvoice.setPrice(productInvoice.getPrice());
      newProductInvoice.setProductId(productInvoice.getProductId());
      newProductInvoice.setUtility(productInvoice.getUtility());
      newProductInvoice = productInvoiceRepository.save(newProductInvoice);
      return new ResponseEntity<>(newProductInvoice, HttpStatus.CREATED);
    } catch (DataIntegrityViolationException e) {
      return ResponseEntity.badRequest().body("Ya existe un producto con el mismo ID de factura.");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor.");
    }
  }

  @PutMapping("/update-product-invoice/{id}")
  public ResponseEntity<ProductInvoice> updateProductInvoice(@PathVariable("id") String id,
      @RequestBody ProductInvoice updatedProductInvoice) {
    try {
      Optional<ProductInvoice> productInvoiceData = productInvoiceRepository.findById(id);
      if (productInvoiceData.isPresent()) {
        ProductInvoice existingProductInvoice = productInvoiceData.get();
        existingProductInvoice.setInvoiceType(updatedProductInvoice.getInvoiceType());
        existingProductInvoice.setDateIssue(updatedProductInvoice.getDateIssue());
        existingProductInvoice.setPrice(updatedProductInvoice.getPrice());
        existingProductInvoice.setAmount(updatedProductInvoice.getAmount());
        existingProductInvoice.setUtility(updatedProductInvoice.getUtility());
        existingProductInvoice.setIdUsuario(updatedProductInvoice.getIdUsuario());
        existingProductInvoice.setInvoiceID(updatedProductInvoice.getInvoiceID());
        existingProductInvoice.setProductId(updatedProductInvoice.getProductId());
        existingProductInvoice.setName(updatedProductInvoice.getName());
        existingProductInvoice.setDescription(updatedProductInvoice.getDescription());
        ProductInvoice savedProductInvoice = productInvoiceRepository.save(existingProductInvoice);
        return new ResponseEntity<>(savedProductInvoice, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/delete-products-invoice-id/{invoiceID}")
  public ResponseEntity<Map<String, String>> deleteProductInvoice(@PathVariable("invoiceID") String invoiceID) {
    Map<String, String> response = new HashMap<>();
    try {
      Optional<ProductInvoice> existingProductInvoice = productInvoiceRepository.findByInvoiceID(invoiceID);
      if (existingProductInvoice.isPresent()) {
        productInvoiceRepository.deleteByInvoiceID(invoiceID);
        response.put("message", "ProductInvoice eliminado con Ã©xito");
        return new ResponseEntity<>(response, HttpStatus.OK);
      } else {
        response.put("message", "No se encontrÃ³ el ProductInvoice con ID: " + invoiceID);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      response.put("message", "Error al eliminar el ProductInvoice");
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/get-product-invoice/{id}")
  public ResponseEntity<ProductInvoice> getProductInvoice(@PathVariable("id") String id,
      @RequestHeader("Authorization") String token) {
    try {
      if (productInvoiceRepository.existsById(id)) {
        ProductInvoice productInvoice = productInvoiceRepository.findById(id).get();
        return new ResponseEntity<>(productInvoice, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/get-products-invoice/{idUsuario}")
  public ResponseEntity<Iterable<ProductInvoice>> getProductInvoicesByUserId(
      @PathVariable("idUsuario") String idUsuario,
      @RequestHeader("Authorization") String token) {
    try {
      Iterable<ProductInvoice> productInvoices = productInvoiceRepository.findByIdUsuario(idUsuario);
      return new ResponseEntity<>(productInvoices, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
