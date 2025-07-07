package com.inv.spring.data.mongodb.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.inv.spring.data.mongodb.model.Product;
import com.inv.spring.data.mongodb.model.Sequence;
import com.inv.spring.data.mongodb.repository.ProductRepository;
import com.inv.spring.data.mongodb.repository.SequenceRepository;

@CrossOrigin(origins = "http://localhost:3000", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
    RequestMethod.DELETE, RequestMethod.OPTIONS })
@RestController
@RequestMapping("/api/products")
public class ProductController {
  private final SequenceRepository sequenceRepository;
  private final ProductRepository productRepository;

  public ProductController(SequenceRepository sequenceRepository, ProductRepository productRepository) {
    this.sequenceRepository = sequenceRepository;
    this.productRepository = productRepository;
  }

  @PostMapping("/add-product")
  public ResponseEntity<Product> addProduct(@RequestBody Product product) {
    try {
      Sequence sequence = sequenceRepository.findById("sequenceProductId")
          .orElseThrow(() -> new RuntimeException("No se pudo encontrar la secuencia"));
      int sequenceValue = sequence.getSequenceValue() + 1;
      sequence.setSequenceValue(sequenceValue);
      sequenceRepository.save(sequence);
      product.setProductId(sequenceValue);
      Product newProduct = productRepository.save(product);
      return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
    }
  }

  @PutMapping("/update-product/{id}")
  public ResponseEntity<Product> updateProduct(@PathVariable("id") String id, @RequestBody Product updatedProduct) {
    try {
      Optional<Product> productData = productRepository.findById(id);
      if (productData.isPresent()) {
        Product existingProduct = productData.get();
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setAmount(updatedProduct.getAmount());
        existingProduct.setUtility(updatedProduct.getUtility());
        Product savedProduct = productRepository.save(existingProduct);
        System.out.println("mensaje");
        System.out.println(savedProduct);
        return new ResponseEntity<>(savedProduct, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/delete-product/{id}")
  public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable("id") String id) {
    Map<String, String> response = new HashMap<>();
    try {
      Optional<Product> existingProduct = productRepository.findById(id);
      if (existingProduct.isPresent()) {
        productRepository.deleteById(id);
        response.put("message", "Product eliminado con Ã©xito");
        return new ResponseEntity<>(response, HttpStatus.OK);
      } else {
        response.put("message", "No se encontrÃ³ el producto con ID: " + id);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      response.put("message", "Error al eliminar el producto");
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/get-product/{id}")
  public ResponseEntity<Product> getProduct(@PathVariable("id") String id,
      @RequestHeader("Authorization") String token) {
    try {
      if (productRepository.existsById(id)) {
        Product product = productRepository.findById(id).get();
        return new ResponseEntity<>(product, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/get-products/{idUsuario}")
  public ResponseEntity<Iterable<Product>> getProductsByUserId(@PathVariable("idUsuario") String idUsuario,
      @RequestHeader("Authorization") String token) {
    try {
      Iterable<Product> products = productRepository.findByIdUsuario(idUsuario);
      return new ResponseEntity<>(products, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PutMapping("/update-product-amount/{productId}")
  public ResponseEntity<Product> updateProduct(@PathVariable("productId") int productId,
      @RequestBody Product updatedProduct) {
    try {
      Optional<Product> productData = productRepository.findByProductId(productId);
      if (productData.isPresent()) {
        Product existingProduct = productData.get();
        existingProduct.setName(updatedProduct.getName());
        existingProduct.setDescription(updatedProduct.getDescription());
        existingProduct.setPrice(updatedProduct.getPrice());
        existingProduct.setAmount(updatedProduct.getAmount());
        existingProduct.setUtility(updatedProduct.getUtility());
        Product savedProduct = productRepository.save(existingProduct);
        System.out.println("mensaje");
        System.out.println(savedProduct);
        return new ResponseEntity<>(savedProduct, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
      }
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
