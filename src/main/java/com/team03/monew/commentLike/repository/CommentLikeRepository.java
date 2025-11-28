package com.team03.monew.commentLike.repository;

import com.team03.monew.commentLike.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {

    // 최근 좋아요 조회 (최대 10개) ... 추신 습관적으로 해버렸습니다. 죄송합니다.
    @Modifying
    @Query("SELECT c FROM CommentLike c ORDER BY c.creationAt DESC LIMIT 10")
    CommentLike findByCommentIdAndUserId(
            @Param("id") UUID commentId,
            @Param("userId") UUID userId
    );

    // 좋아요 취소
    void deleteByCommentIdAndUserId(UUID commentId, UUID userId);
}
