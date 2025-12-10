package com.team03.monew.comment.repository;

import com.team03.monew.comment.domain.Comment;
import com.team03.monew.comment.dto.CommentActivityDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID>, CommentRepositoryCustom {
    Long countByArticleIdAndDeletedAtIsNull(UUID articleId);

    @Query("SELECT c FROM Comment c WHERE c.user_id = :user_id ORDER BY c.created_at DESC LIMIT 10")
    List<CommentActivityDto> findTopTenByUserIdOrderByCreatedAtDesc(
            @Param("user_id") UUID userId
    );
}
