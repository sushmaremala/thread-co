package com.example.thread_co;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface OrderRepository extends MongoRepository<OrderDocument, String> {
    List<OrderDocument> findByUserEmail(String userEmail);
}
