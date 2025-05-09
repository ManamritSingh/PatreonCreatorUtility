package com.patreon.backend;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.patreon.api.models.Member;

public interface MemberRepository extends JpaRepository<Member, String> {
    List<Member> findByIsActiveTrue();
    List<Member> findByIsTestTrue();
    
    @Query(value = "SELECT 'Tier 1' UNION SELECT 'Tier 2' UNION SELECT 'Tier 3' UNION SELECT 'All'", nativeQuery = true)
    List<String> findAllTiers();

    List<Member> findByTierIdIn(List<String> selectedTiers);
    List<Member> findByTierId(String tierId);
}
