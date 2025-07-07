package com.inv.spring.data.mongodb.repository;

import com.inv.spring.data.mongodb.model.ProductInvoice;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface ProductInvoiceRepository extends CrudRepository<ProductInvoice, String> {
  Iterable<ProductInvoice> findByIdUsuario(String idUsuario);

  Optional<ProductInvoice> findByInvoiceID(String invoiceID);

  void deleteByInvoiceID(String invoiceID);
}
