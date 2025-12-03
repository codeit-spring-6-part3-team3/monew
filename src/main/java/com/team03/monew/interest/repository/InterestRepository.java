package com.team03.monew.interest.repository;

import com.team03.monew.interest.domain.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface InterestRepository extends JpaRepository<Interest, UUID>, InterestRepositoryCustom {
}
