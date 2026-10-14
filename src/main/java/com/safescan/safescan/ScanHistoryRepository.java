
package com.safescan.safescan;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanHistoryRepository extends JpaRepository<ScanHistory, Long> {

    List<ScanHistory> findAllByUser_IdOrderByScannedAtDesc(Long userId);

    void deleteAllByUser_Id(Long userId);

    long countByCategoryIgnoreCase(String category);

    List<ScanHistory> findTop10ByOrderByScannedAtDesc();
}

