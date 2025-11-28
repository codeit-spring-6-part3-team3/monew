package com.team03.monew.subscribe.repository;

import com.team03.monew.subscribe.domain.Subscribe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubscribeRepository extends JpaRepository<Subscribe, UUID> {
}
