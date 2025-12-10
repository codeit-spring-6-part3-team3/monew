package com.team03.monew.comment.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team03.monew.comment.domain.QComment;
import com.team03.monew.comment.dto.CommentDto;
import com.team03.monew.comment.dto.CursorPageRequestCommentDto;
import com.team03.monew.commentlike.domain.QCommentLike;
import com.team03.monew.user.domain.QUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QComment comment = QComment.comment;
    private final QUser user = QUser.user;
    private final QCommentLike commentLike = QCommentLike.commentLike;

    @Override
    public Slice<CommentDto> findByCursor(CursorPageRequestCommentDto request) {
        int limit = request.limit() != null ? request.limit() : 20;

        List<CommentDto> comments = queryFactory
                .select(Projections.constructor(CommentDto.class,
                        comment.id,
                        comment.articleId,
                        comment.userId,
                        user.nickname,
                        comment.content,
                        comment.likeCount,
                        commentLike.likedBy.eq(request.userId()),
                        comment.createdAt
                ))
                .from(comment)
                .leftJoin(user).on(comment.userId.eq(user.id))
                .leftJoin(commentLike).on(
                        commentLike.commentId.eq(comment.id)
                                .and(commentLike.likedBy.eq(request.userId()))
                )
                .where(
                        articleIdEq(request.articleId()),
                        cursorCondition(request),
                        afterCondition(request.after()),
                        comment.deletedAt.isNull()
                )
                .orderBy(getOrderSpecifier(request))
                .limit(limit + 1)
                .fetch();

        boolean hasNext = comments.size() > limit;
        if (hasNext) {
            comments.remove(comments.size() - 1);
        }

        return new SliceImpl<>(comments, PageRequest.of(0, limit), hasNext);
    }

    private BooleanExpression articleIdEq(UUID articleId) {
        return articleId != null ? comment.articleId.eq(articleId) : null;
    }

    private BooleanExpression cursorCondition(CursorPageRequestCommentDto request) {
        if (request.cursor() == null) {
            return null;
        }

        String orderBy = request.orderBy() != null ? request.orderBy() : "created_at";
        boolean isAsc = "asc".equalsIgnoreCase(request.direction());

        if ("like_count".equals(orderBy)) {
            Long cursorLikeCount = Long.parseLong(request.cursor());
            return isAsc ? comment.likeCount.gt(cursorLikeCount)
                    : comment.likeCount.lt(cursorLikeCount);
        } else {
            LocalDateTime cursorDateTime = LocalDateTime.parse(request.cursor());
            return isAsc ? comment.createdAt.gt(cursorDateTime)
                    : comment.createdAt.lt(cursorDateTime);
        }
    }

    private BooleanExpression afterCondition(LocalDateTime after) {
        return after != null ? comment.createdAt.gt(after) : null;
    }

    private OrderSpecifier<?> getOrderSpecifier(CursorPageRequestCommentDto request) {
        String orderBy = request.orderBy() != null ? request.orderBy() : "created_at";
        boolean isAsc = "asc".equalsIgnoreCase(request.direction());

        if ("like_count".equals(orderBy)) {
            return isAsc ? comment.likeCount.asc() : comment.likeCount.desc();
        }

        return isAsc ? comment.createdAt.asc() : comment.createdAt.desc();
    }
}
