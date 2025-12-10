package com.team03.monew.commentLike.repository;

import com.team03.monew.commentLike.domain.CommentLike;
import com.team03.monew.commentLike.dto.CommentLikeActivityDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {

    // 최근 좋아요 조회 (최대 10개)
    @Query("SELECT cl FROM CommentLike cl WHERE cl.userId = :userId ORDER BY cl.creationAt DESC LIMIT 10")
    List<CommentLikeActivityDto> findTopTenByUserIdOrderByCreationAtDesc(
            @Param("userId") UUID userId
    );

    // 좋아요 취소
    void deleteByCommentIdAndUserId(UUID commentId, UUID userId);
}
