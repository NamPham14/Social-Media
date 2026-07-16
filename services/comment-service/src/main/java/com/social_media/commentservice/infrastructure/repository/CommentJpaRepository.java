package com.social_media.commentservice.infrastructure.repository;

import com.social_media.commentservice.domain.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentJpaRepository extends JpaRepository<Comment, UUID> {
    @Query("""
            select c from Comment c
            where c.postId = :postId
              and (c.deleted = false or exists (
                  select r.id from Comment r where r.parentId = c.id and r.deleted = false
              ))
            """)
    Page<Comment> findVisibleByPostId(@Param("postId") UUID postId, Pageable pageable);

    boolean existsByParentIdAndDeletedFalse(UUID parentId);
}
