package ru.gavrilovegor519.nodehistj_historic_nodelists.repo;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.gavrilovegor519.nodehistj_historic_nodelists.entity.NodelistEntity;

public interface NodelistEntityRepository extends MongoRepository<NodelistEntity, ObjectId> {
    NodelistEntity findByNodelistYearAndNodelistName(Integer year, String name);

    boolean existsByNodelistYearAndNodelistName(Integer year, String name);
}