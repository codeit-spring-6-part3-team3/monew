
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
    public Slice<Notification> findNotificationsWithCursor(UUID userId, String cursor, int size) {
        List<Notification> notifications = queryFactory
                .selectFrom(notification)
                .where(
                        notification.userId.eq(userId),
                        notification.isChecked.eq(false),
                        cursorCondition(cursor)
                )
                .orderBy(notification.creationAt.desc())
                .limit(size + 1)
                .fetch();

        return createSlice(notifications, size);
    }

    // 위 아래 두 메서드에 notification.isChecked.eq(false) 추가. 사유: unchecked를 포함한 페이지네이션 제한이 발생

    @Override
    public Slice<Notification> findNotifications(UUID userId, int size) {
        List<Notification> notifications = queryFactory
                .selectFrom(notification)
                .where(
                        notification.userId.eq(userId),
                        notification.isChecked.eq(false)
                )
                .orderBy(notification.creationAt.desc())
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
            return notification.creationAt.lt(cursorDateTime);
        } catch (Exception e) {
            return null;
        }
    }

    private Slice<Notification> createSlice(List<Notification> notifications, int size) {
        boolean hasNext = notifications.size() > size;

        if (hasNext) {
            notifications.remove(notifications.size() - 1);
        }

        return new SliceImpl<>(notifications, org.springframework.data.domain.Pageable.unpaged(), hasNext);
    }
}
