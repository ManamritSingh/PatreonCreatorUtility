package com.patreon.backend;

import com.patreon.backend.models.TierSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface TierSnapshotRepository extends JpaRepository<TierSnapshot, Long> {
    List<TierSnapshot> findByTimestamp(LocalDate timestamp);
    List<TierSnapshot> findByIsMock(boolean isMock);
}
