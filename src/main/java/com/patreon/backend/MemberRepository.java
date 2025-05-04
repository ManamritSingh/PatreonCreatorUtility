package com.patreon.backend;

import com.patreon.api.models.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, String> {
    List<Member> findByIsActiveTrue();
    List<Member> findByIsTestTrue();
}
