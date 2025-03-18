package ru.gavrilovegor519.nodehistj_historic_nodelists.repo;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import ru.gavrilovegor519.nodehistj_historic_nodelists.entity.NodelistEntity;

import java.util.List;

public interface NodelistEntityRepository extends CrudRepository<NodelistEntity, Long> {
    @Query("SELECT * FROM nodelist WHERE nodelist_year = :year AND nodelist_name = :nodelistName")
    List<NodelistEntity> get(Integer year, String nodelistName);

    @Query("SELECT * FROM nodelist WHERE nodelist_year = :year AND " +
           "nodelist_name = :nodelistName AND zone = :zone " +
           "ORDER BY zone ASC, network ASC, node ASC")
    List<NodelistEntity> get(Integer year, String nodelistName, Integer zone);

    @Query("SELECT * FROM nodelist WHERE nodelist_year = :year " +
           "AND nodelist_name = :nodelistName " +
           "AND zone = :zone AND network = :network " +
           "ORDER BY zone ASC, network ASC, node ASC")
    List<NodelistEntity> get(Integer year, String nodelistName, Integer zone, Integer network);

    @Query("SELECT * FROM nodelist WHERE nodelist_year = :year " +
           "AND nodelist_name = :nodelistName AND zone = :zone " +
           "AND network = :network AND node = :node LIMIT 1")
    NodelistEntity get(Integer year, String nodelistName, Integer zone, Integer network, Integer node);

    @Query("SELECT * FROM nodelist WHERE nodelist_year = (SELECT MAX(nodelist_year) FROM nodelist) " +
           "AND nodelist_name = (SELECT MAX(nodelist_name) FROM nodelist) " +
           "ORDER BY zone ASC, network ASC, node ASC")
    List<NodelistEntity> get();

    @Query("SELECT * FROM nodelist WHERE nodelist_year = (SELECT MAX(nodelist_year) FROM nodelist) AND " +
           "nodelist_name = (SELECT MAX(nodelist_name) FROM nodelist) AND zone = :zone " +
           "ORDER BY zone ASC, network ASC, node ASC")
    List<NodelistEntity> get(Integer zone);

    @Query("SELECT * FROM nodelist WHERE nodelist_year = (SELECT MAX(nodelist_year) FROM nodelist) AND " +
           "nodelist_name = (SELECT MAX(nodelist_name) FROM nodelist) AND zone = :zone AND " +
           "network = :network ORDER BY zone ASC, network ASC, node ASC")
    List<NodelistEntity> get(Integer zone, Integer network);

    @Query("SELECT * FROM nodelist WHERE nodelist_year = (SELECT MAX(nodelist_year) FROM nodelist) AND " +
           "nodelist_name = (SELECT MAX(nodelist_name) FROM nodelist) AND zone = :zone AND " +
           "network = :network AND node = :node LIMIT 1")
    NodelistEntity get(Integer zone, Integer network, Integer node);

    @Query("SELECT EXISTS(SELECT 1 FROM nodelist WHERE nodelist_year = :year AND nodelist_name = :nodelistName " +
           "AND zone = :zone AND network = :network AND node = :node)")
    boolean isExist(Integer year, String nodelistName, Integer zone, Integer network, Integer node);
}