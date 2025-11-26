package com.team03.monew.notification.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "userId", nullable = false)
    private UUID userId;

    @Column(name = "context", nullable = false)
    private String context;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource", length = 10, nullable = false)
    private NoticeResourceType resource;

    @Column(name = "isChecked", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isChecked;

    @CreationTimestamp
    @Column(name = "creationAt", nullable = false, updatable = false)
    private LocalDateTime creationAt;

    private Notification(
            UUID id,
            UUID userId,
            String context,
            NoticeResourceType resource,
            boolean isChecked,
            LocalDateTime creationAt
    ) {
        this.id = id;
        this.userId = userId;
        this.context = context;
        this.resource = resource;
        this.isChecked = isChecked;
        this.creationAt = creationAt;
    }

    /*
    * 내부 로직에 따른 변경이 있는 것
    * userId   : 받는 대상 (요청자x)
    * context  : 보내는 내용 (댓글 - 유저 닉네임, 뉴스 - 수집한 뉴스 중 등록된 건 수)
    * resource : 무엇인지 (댓글, 뉴스)
    * */
    private Notification(
            UUID userId,
            String context,
            NoticeResourceType resource
    ) {
        this(
                null,
                userId,
                context,
                resource,
                false,
                null
        );
    }

    public static Notification from(
            UUID userId,
            String context,
            NoticeResourceType resource
    ) {
        return new Notification(userId, context, resource);
    }

    public boolean isExpired() {
        return isChecked && creationAt.isBefore(LocalDateTime.now().minusWeeks(1));
    }

    public Notification check() {
        this.isChecked = true;
        return this;
    }
}
