package com.team03.monew.comment.repository;

import com.team03.monew.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID>, CommentRepositoryCustom {

    Long countByArticleIdAndDeletedAtIsNull(UUID articleId);
}
