package com.social_media.postservice.infrastructure.repository;


import com.social_media.postservice.infrastructure.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostJpaRepository extends JpaRepository<PostEntity, UUID> {

    @Query("SELECT p FROM PostEntity p WHERE LOWER(p.caption) LIKE LOWER(:keyword)")
    Page<PostEntity> findByCaptionLikeIgnoreCase(@Param("keyword") String keyword,  Pageable pageable);


    Page<PostEntity> findPostEntityByUserId(UUID userId, Pageable pageable);

    @Modifying
    @Query("UPDATE PostEntity p SET p.author.name = :authorName, p.author.avatarUrl = :authorAvatarUrl WHERE p.userId = :userId")
    void updateAuthorInfo(@Param("userId") UUID userId,
                          @Param("authorName") String authorName,
                          @Param("authorAvatarUrl") String authorAvatarUrl);

    Page<PostEntity> findByUserIdIn(List<UUID> userIds, Pageable pageable);

}
