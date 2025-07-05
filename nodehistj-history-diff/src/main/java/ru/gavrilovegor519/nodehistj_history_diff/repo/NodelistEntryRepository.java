package ru.gavrilovegor519.nodehistj_history_diff.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gavrilovegor519.nodehistj_history_diff.entity.NodelistEntry;

public interface NodelistEntryRepository extends JpaRepository<NodelistEntry, Long> {
    boolean existsByNodelistYearAndNodelistName(Integer nodelistYear, String nodelistName);
}