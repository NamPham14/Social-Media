package com.social_media.commentservice.infrastructure.repository;

import com.social_media.commentservice.domain.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

public interface CommentJpaRepository extends JpaRepository<Comment, UUID> {
    @Query("""
            select c from Comment c
            where c.postId = :postId
              and c.parentId is null
              and (c.deleted = false or exists (
                  select r.id from Comment r where r.parentId = c.id and r.deleted = false
              ))
            """)
    Page<Comment> findVisibleByPostId(@Param("postId") UUID postId, Pageable pageable);

    Page<Comment> findByParentIdAndDeletedFalse(UUID parentId, Pageable pageable);

    boolean existsByParentIdAndDeletedFalse(UUID parentId);

    long countByParentIdAndDeletedFalse(UUID parentId);

    @Query("""
            select c.parentId as parentId, count(c.id) as replyCount
            from Comment c
            where c.parentId in :parentIds and c.deleted = false
            group by c.parentId
            """)
    List<ReplyCountProjection> countActiveRepliesByParentIds(@Param("parentIds") Collection<UUID> parentIds);

    long countByPostIdAndDeletedFalse(UUID postId);

    @Query("""
            select c.postId as postId, count(c.id) as commentCount
            from Comment c
            where c.postId in :postIds and c.deleted = false
            group by c.postId
            """)
    List<CommentCountProjection> countActiveByPostIds(@Param("postIds") Collection<UUID> postIds);

    @Query("select c.id from Comment c where c.postId = :postId and c.deleted = false")
    List<UUID> findActiveIdsByPostId(@Param("postId") UUID postId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Comment c set c.deleted = true, c.updatedAt = CURRENT_TIMESTAMP " +
            "where c.postId = :postId and c.deleted = false")
    int softDeleteAllByPostId(@Param("postId") UUID postId);


    @Modifying
    @Query("UPDATE Comment c SET c.authorName = COALESCE(:authorName, c.authorName), " +
            "c.authorAvatarUrl = COALESCE(:authorAvatarUrl, c.authorAvatarUrl) WHERE c.userId = :userId")
    void updateAuthorInfo(@Param("userId") UUID userId,
                          @Param("authorName") String authorName,
                          @Param("authorAvatarUrl") String authorAvatarUrl);

}
