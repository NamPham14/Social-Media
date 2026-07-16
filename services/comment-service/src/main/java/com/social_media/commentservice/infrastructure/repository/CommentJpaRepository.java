package com.social_media.commentservice.infrastructure.repository;

import com.social_media.commentservice.domain.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
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

    long countByPostIdAndDeletedFalse(UUID postId);

    @Query("""
            select c.postId as postId, count(c.id) as commentCount
            from Comment c
            where c.postId in :postIds and c.deleted = false
            group by c.postId
            """)
    List<CommentCountProjection> countActiveByPostIds(@Param("postIds") Collection<UUID> postIds);
}
