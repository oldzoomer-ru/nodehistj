package ru.gavrilovegor519.nodehistj.repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.gavrilovegor519.nodehistj.entity.NodelistEntity;

public interface NodelistEntityRepository extends MongoRepository<NodelistEntity, ObjectId> {
    NodelistEntity findByNodelistYearAndNodelistName(Integer year, String name);
}