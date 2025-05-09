package com.patreon.utils;

import com.patreon.backend.models.TierSnapshot;
import com.patreon.backend.TierSnapshotRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class DatabaseExporter {

    private final TierSnapshotRepository repository;

    public DatabaseExporter(TierSnapshotRepository repository) {
        this.repository = repository;
    }

    public void export(List<TierSnapshot> snapshots) {
        repository.saveAll(snapshots);
        System.out.println("TierSnapshots saved to database.");
    }
}
