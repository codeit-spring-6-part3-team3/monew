package com.team03.monew.user.repository;

import com.team03.monew.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    // << Sring 에서 UUID로 바꿨습니다.
    boolean existsByEmail(String email);
}