package com.team03.monew.articleviews.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.dsl.StringTemplate;

import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.annotations.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleViews is a Querydsl query type for ArticleViews
 */
@SuppressWarnings("this-escape")
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticleViews extends EntityPathBase<ArticleViews> {

    private static final long serialVersionUID = 1007728253L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QArticleViews articleViews = new QArticleViews("articleViews");

    public final com.team03.monew.article.domain.QArticle article;

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    public final com.team03.monew.user.domain.QUser user;

    public QArticleViews(String variable) {
        this(ArticleViews.class, forVariable(variable), INITS);
    }

    public QArticleViews(Path<? extends ArticleViews> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QArticleViews(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QArticleViews(PathMetadata metadata, PathInits inits) {
        this(ArticleViews.class, metadata, inits);
    }

    public QArticleViews(Class<? extends ArticleViews> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new com.team03.monew.article.domain.QArticle(forProperty("article"), inits.get("article")) : null;
        this.user = inits.isInitialized("user") ? new com.team03.monew.user.domain.QUser(forProperty("user")) : null;
    }

}

