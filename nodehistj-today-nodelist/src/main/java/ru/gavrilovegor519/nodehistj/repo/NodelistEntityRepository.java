package ru.gavrilovegor519.nodehistj.repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.gavrilovegor519.nodehistj.entity.NodelistEntity;

import java.time.Year;

public interface NodelistEntityRepository extends MongoRepository<NodelistEntity, ObjectId> {
    boolean existsByNodelistYearAndNodelistName(Year year, String name);
    NodelistEntity findByNodelistYearAndNodelistName(Year year, String name);
}