package com.solstice.styleservice.repository;

import com.solstice.styleservice.model.Style;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface StyleRepository extends MongoRepository<Style, String> {

    Style getById(String id);

    Style getByName(String name);

    void deleteById(String id);

    void deleteByName(String name);
}