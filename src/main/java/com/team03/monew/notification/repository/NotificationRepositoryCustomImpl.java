
package com.team03.monew.notification.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.team03.monew.notification.domain.QNotification.notification;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryCustomImpl implements NotificationRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Notification> findNotificationsWithCursor(UUID userId, LocalDateTime cursor, int size) {
        List<Notification> notifications = queryFactory
                .selectFrom(notification)
                .where(
                        notification.userId.eq(userId),
                        notification.creationAt.lt(cursor)
                )
                .orderBy(notification.creationAt.desc())
                .limit(size + 1)
                .fetch();

        return createSlice(notifications, size);
    }

    @Override
    public Slice<Notification> findNotifications(UUID userId, int size) {
        List<Notification> notifications = queryFactory
                .selectFrom(notification)
                .where(
                        notification.userId.eq(userId)
                )
                .orderBy(notification.creationAt.desc())
                .limit(size + 1)
                .fetch();

        return createSlice(notifications, size);
    }

    private Slice<Notification> createSlice(List<Notification> notifications, int size) {
        boolean hasNext = notifications.size() > size;

        if (hasNext) {
            notifications.remove(notifications.size() - 1);
        }

        return new SliceImpl<>(notifications, org.springframework.data.domain.Pageable.unpaged(), hasNext);
    }
}