
package com.team03.monew.notification.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
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
    public Slice<Notification> findNotificationsWithCursor(UUID userId, String cursor, int size, String after) {
        List<Notification> notifications = queryFactory
                .selectFrom(notification)
                .where(
                        notification.userId.eq(userId),
                        notification.isChecked.eq(false),
                        cursorCondition(cursor)
                )
                .orderBy(notification.createdAt.desc())
                .limit(size + 1)
                .fetch();

        return createSlice(notifications, size, after);
    }

    @Override
    public Slice<Notification> findNotifications(UUID userId, int size, String after) {
        List<Notification> notifications = queryFactory
                .selectFrom(notification)
                .where(
                        notification.userId.eq(userId),
                        notification.isChecked.eq(false)
                )
                .orderBy(notification.createdAt.desc())
                .limit(size + 1)
                .fetch();

        return createSlice(notifications, size, after);
    }

    private BooleanExpression cursorCondition(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return null;
        }
        
        try {
            LocalDateTime cursorDateTime = LocalDateTime.parse(cursor);
            return notification.createdAt.lt(cursorDateTime);
        } catch (Exception e) {
            return null;
        }
    }

    private Slice<Notification> createSlice(List<Notification> notifications, int size, String after) {
        boolean hasNext = after == null || notifications.size() <= size;

        if (hasNext) {
            notifications.remove(notifications.size() - 1);
        }

        return new SliceImpl<>(notifications, org.springframework.data.domain.Pageable.unpaged(), hasNext);
    }
}
