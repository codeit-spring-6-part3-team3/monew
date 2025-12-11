package com.team03.monew.commentlike.repository;

import com.team03.monew.comment.domain.Comment;
import com.team03.monew.commentlike.domain.CommentLike;
import com.team03.monew.commentlike.dto.CommentLikeActivityDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {

    /*/ 최근 좋아요 조회 (최대 10개)
    @Query("SELECT cl FROM CommentLike cl WHERE cl.commentUserId = :userId ORDER BY cl.createdAt DESC LIMIT 10")
    List<CommentLikeActivityDto> findTopTenByUserIdOrderByCreatedAtDesc(
            @Param("user_id") UUID userId
    );
    */

    List<CommentLike> findTop10ByLikedByOrderByCreatedAtDesc(UUID likedBy);


    boolean existsByCommentIdAndCommentUserId(UUID commentId, UUID userId);

    // 좋아요 취소
    void deleteByCommentIdAndCommentUserId(UUID commentId, UUID userId);

    Long countCommentLikeByCommentId(UUID commentId);
}
