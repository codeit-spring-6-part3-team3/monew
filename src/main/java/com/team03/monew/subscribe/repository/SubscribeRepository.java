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

    List<Subscribe> findTop10ByUserIdOrderByCreatedAtDesc(UUID userId);

    @Query("select s.userId from Subscribe s where s.interestId = :interestId")
    List<UUID> findUserIdsByInterestId(@Param("interestId") UUID interestId);

    List<Subscribe> findByUserIdAndInterestIdIn(UUID userId, List<UUID> interestIds);
}
