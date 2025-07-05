package ru.gavrilovegor519.nodehistj_history_diff.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ru.gavrilovegor519.nodehistj_history_diff.entity.NodelistEntry;

@Repository
public interface NodeEntryRepository extends JpaRepository<NodelistEntry, Long> {
    
    @Query("SELECT ne FROM NodelistEntry ne WHERE ne.nodelistYear = :nodelistYear AND ne.nodelistName = :nodelistName")
    List<NodelistEntry> findByNodelistYearAndName(@Param("nodelistYear") Integer nodelistYear,
                                                 @Param("nodelistName") String nodelistName);

    @Query("SELECT DISTINCT ne.nodelistYear, ne.nodelistName FROM NodelistEntry ne ORDER BY ne.nodelistYear DESC, ne.nodelistName DESC")
    List<Object[]> findAllNodelistVersions();
}