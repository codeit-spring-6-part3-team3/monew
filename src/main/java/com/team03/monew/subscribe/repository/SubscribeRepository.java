package com.team03.monew.subscribe.repository;

import com.team03.monew.subscribe.domain.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubscribeRepository extends JpaRepository<Subscribe, UUID> {

    Optional<Subscribe> findByUserIdAndInterestId(UUID userId, UUID interestId);

    Boolean existsByUserIdAndInterestId(UUID userId, UUID interestId);

    List<Subscribe> findByUserId(UUID userId);

    List<Subscribe> findByUserIdAndInterestIdIn(UUID userId, List<UUID> interestIds);

    List<Subscribe> findByInterestIdIn(List<UUID> interestIds);
}
