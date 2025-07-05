package ru.oldzoomer.nodehistj_historic_nodelists.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.oldzoomer.nodehistj_historic_nodelists.entity.NodelistEntry;

public interface NodelistEntryRepository extends JpaRepository<NodelistEntry, Long> {
    boolean existsByNodelistYearAndNodelistName(Integer nodelistYear, String nodelistName);

    List<NodelistEntry> findByNodelistYearBetween(Integer startYear, Integer endYear);
}