package com.team03.monew.subscribe.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
public class Subscribe {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID interestId;

    @CreatedDate
    private LocalDateTime createdAt;

    public Subscribe() {}

    @Builder
    public Subscribe(UUID userId, UUID interestId) {
        this.userId = userId;
        this.interestId = interestId;
    }
}
