package com.team03.monew.commentlike.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;


/**
 * QCommentLike is a Querydsl query type for CommentLike
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCommentLike extends EntityPathBase<CommentLike> {

    private static final long serialVersionUID = -1226569451L;

    public static final QCommentLike commentLike = new QCommentLike("commentLike");

    public final ComparablePath<java.util.UUID> articleId = createComparable("articleId", java.util.UUID.class);

    public final StringPath commentContent = createString("commentContent");

    public final DateTimePath<java.time.LocalDateTime> commentCreatedAt = createDateTime("commentCreatedAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> commentId = createComparable("commentId", java.util.UUID.class);

    public final NumberPath<Long> commentLikeCount = createNumber("commentLikeCount", Long.class);

    public final ComparablePath<java.util.UUID> commentUserId = createComparable("commentUserId", java.util.UUID.class);

    public final StringPath commentUserNickname = createString("commentUserNickname");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final ComparablePath<java.util.UUID> likedBy = createComparable("likedBy", java.util.UUID.class);

    public QCommentLike(String variable) {
        super(CommentLike.class, forVariable(variable));
    }

    public QCommentLike(Path<? extends CommentLike> path) {
        super(path.getType(), path.getMetadata());
    }

    public QCommentLike(PathMetadata metadata) {
        super(CommentLike.class, metadata);
    }

}

