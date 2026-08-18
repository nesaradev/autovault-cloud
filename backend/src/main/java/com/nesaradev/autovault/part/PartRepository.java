package com.nesaradev.autovault.part;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PartRepository extends MongoRepository<Part, String> {
    Optional<Part> findByPartNumber(String partNumber);

    boolean existsByPartNumber(String partNumber);

    Page<Part> findByActiveTrue(Pageable pageable);
}
