
package com.team03.monew.notification.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.notification.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    public Slice<Notification> findNotificationsWithCursor(UUID userId, String cursor, int size, LocalDateTime after) {
        List<Notification> notifications = queryFactory
                .selectFrom(notification)
                .where(
                        notification.userId.eq(userId),
                        notification.isChecked.eq(false),
                        cursorCondition(cursor),
                        afterCondition(after)
                )
                .orderBy(notification.createdAt.desc())
                .limit(size + 1)
                .fetch();

        return createSlice(notifications, size);
    }

    @Override
    public Slice<Notification> findNotifications(UUID userId, int size, LocalDateTime after) {
        List<Notification> notifications = queryFactory
                .selectFrom(notification)
                .where(
                        notification.userId.eq(userId),
                        notification.isChecked.eq(false),
                        afterCondition(after)
                )
                .orderBy(notification.createdAt.desc())
                .limit(size + 1)
                .fetch();

        return createSlice(notifications, size);
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

    private BooleanExpression afterCondition(LocalDateTime after) {
        return after != null ? notification.createdAt.gt(after) : null;
    }

    private Slice<Notification> createSlice(List<Notification> notifications, int size) {
        boolean hasNext = notifications.size() > size;

        if (hasNext) {
            notifications = notifications.subList(0, size);
        }

        return new SliceImpl<>(notifications, PageRequest.of(0, size), hasNext);
    }
}
