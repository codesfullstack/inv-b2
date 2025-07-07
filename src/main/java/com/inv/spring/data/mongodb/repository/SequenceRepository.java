package com.inv.spring.data.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.inv.spring.data.mongodb.model.Sequence;

public interface SequenceRepository extends MongoRepository<Sequence, String> {
  Sequence findByid(String id);
}
