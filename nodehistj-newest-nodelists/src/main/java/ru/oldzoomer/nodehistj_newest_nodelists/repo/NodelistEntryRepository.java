package ru.oldzoomer.nodehistj_newest_nodelists.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.oldzoomer.nodehistj_newest_nodelists.entity.NodelistEntry;

public interface NodelistEntryRepository extends JpaRepository<NodelistEntry, Long> {
    boolean existsByNodelistYearAndNodelistName(Integer nodelistYear, String nodelistName);
}